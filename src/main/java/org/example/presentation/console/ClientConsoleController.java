package org.example.presentation.console;

import org.example.presentation.socket.SocketChatClient;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Scanner;

public class ClientConsoleController {
    private final ClientConsoleView view;

    public ClientConsoleController(ClientConsoleView view) {
        this.view = view;
    }

    public void start(String host, int port, String[] args) {
        view.showConnectionAttempt(host, port);

        try (SocketChatClient client = new SocketChatClient(host, port);
             Scanner scanner = new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8)) {

            view.showConnected();

            if (args.length > 0 && "--disconnect".equalsIgnoreCase(args[0])) {
                view.showDisconnectedWithoutMessage();
                return;
            }

            while (!login(client, scanner)) {
                view.showLoginRetry();
            }

            String currentRoom = "general";
            view.showCommands();

            while (true) {
                view.promptCommand();
                if (!scanner.hasNextLine()) {
                    view.showInputClosed();
                    break;
                }

                String command = scanner.nextLine().trim().toUpperCase();
                if ("QUIT".equals(command)) {
                    String response = client.sendAndRead("QUIT||");
                    view.showReceived(response);
                    view.showClientClosing();
                    break;
                }

                if ("ROOMS".equals(command)) {
                    view.showReceived(client.sendAndRead("ROOMS||"));
                } else if ("TEXT".equals(command)) {
                    view.promptRoomMessage(currentRoom);
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    String payload = scanner.nextLine();
                    view.showReceived(client.sendAndRead("TEXT|" + currentRoom + "|" + payload));
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
                    view.showReceived(client.sendAndRead("JOIN_ROOM|" + room + "|"));
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
                    view.showReceived(client.sendAndRead("PRIVATE|" + recipient + "|" + payload));
                } else {
                    view.showUnknownCommand();
                }
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
        return response.contains("|LOGIN|");
    }
}
