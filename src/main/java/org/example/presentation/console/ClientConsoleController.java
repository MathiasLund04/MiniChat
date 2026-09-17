package org.example.presentation.console;

import org.example.presentation.socket.SocketChatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientConsoleController {
    private final ClientConsoleView view;

    public ClientConsoleController(ClientConsoleView view) {
        this.view = view;
    }

    public void start(String host, int port, String[] args) {
        view.showConnectionAttempt(host, port);

        try (SocketChatClient client = new SocketChatClient(host, port);
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            view.showConnected();

            if (args.length > 0 && "--disconnect".equalsIgnoreCase(args[0])) {
                view.showDisconnectedWithoutMessage();
                return;
            }

            String currentRoom = "general";

            while (!login(client, scanner)) {
                if (!scanner.hasNextLine()) {
                    return;
                }
                view.showLoginRetry();
            }

            Thread receiverThread = startReceiverThread(client.getReader());
            boolean quitRequested = false;
            view.showCommands();
            view.promptCommand();

            while (true) {
                if (!scanner.hasNextLine()) {
                    view.showInputClosed();
                    break;
                }

                String command = scanner.nextLine().trim().toUpperCase();
                if ("QUIT".equals(command)) {
                    client.send("QUIT||");
                    view.showClientClosing();
                    quitRequested = true;
                    break;
                }

                if ("ROOMS".equals(command)) {
                    client.send("ROOMS||");
                } else if ("TEXT".equals(command)) {
                    view.promptRoomMessage(currentRoom);
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    String payload = scanner.nextLine();
                    client.send("TEXT|" + currentRoom + "|" + payload);
                } else if ("JOIN_ROOM".equals(command)) {
                    view.promptRoomName();
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    String room = scanner.nextLine().trim();
                    if (room.isBlank()) {
                        view.showMissingRoomName();
                        continue;
                    }
                    client.send("JOIN_ROOM|" + room + "|");
                    currentRoom = room;
                } else if ("PRIVATE".equals(command)) {
                    view.promptRecipient();
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    String recipient = scanner.nextLine().trim();

                    view.promptMessage();
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    String payload = scanner.nextLine();
                    client.send("PRIVATE|" + recipient + "|" + payload);
                } else {
                    view.showUnknownCommand();
                }

            }

            if (quitRequested) {
                awaitReceiverShutdown(receiverThread);
            }
        } catch (ConnectException exception) {
            view.showConnectionError(port);
        } catch (IOException exception) {
            view.showClientError(exception.getMessage());
        }
    }

    private boolean login(SocketChatClient client, Scanner scanner) throws IOException {
        view.promptUsername();
        if (!scanner.hasNextLine()) {
            view.showInputClosed();
            return false;
        }

        String username = scanner.nextLine().trim();
        String response = client.sendAndRead("LOGIN|" + username + "|");
        view.showReceived(response);
        return isLoginAccepted(response);
    }

    private Thread startReceiverThread(BufferedReader reader) {
        Thread receiverThread = new Thread(() -> {
            try {
                String response;
                while ((response = reader.readLine()) != null) {
                    view.showReceived(response);
                    view.promptCommand();
                }
            } catch (IOException exception) {
                view.showServerClosed(exception.getMessage());
            }
        });

        receiverThread.setDaemon(true);
        receiverThread.setName("chat-client-receiver");
        receiverThread.start();
        return receiverThread;
    }

    private void awaitReceiverShutdown(Thread receiverThread) {
        try {
            receiverThread.join();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            view.showInterruptedShutdown();
        }
    }

    private boolean isLoginAccepted(String response) {
        String[] parts = response.split("\\|", 5);
        return parts.length > 1 && "LOGIN".equals(parts[1]);
    }
}
