package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private static final ClientRegistry CLIENT_REGISTRY = new ClientRegistry();

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final MessageParser messageParser = new MessageParser();
    private String username;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
        this.writer = new PrintWriter(
                new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                true
        );
    }

    @Override
    public void run() {
        try {
            String rawMessage;
            while ((rawMessage = reader.readLine()) != null) {
                if (username == null){
                    System.out.println("Modtaget fra klient: " + rawMessage);
                } else {
                    System.out.println("Modtaget fra " + username + ": " + rawMessage);
                }
                handleMessage(messageParser.parse(rawMessage));
            }

            System.out.println("Klienten lukker forbindelsen.");
        } catch (IOException ex) {
            System.out.println("Fejl i ClientHandler: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            sendErrorMessage("", ex.getMessage());
        } finally {
            close();
        }
    }

    public void sendMessage(String message) {
        if (message == null) {
            return;
        }
        writer.println(message);
        writer.flush();
    }

    public String readMessage() throws IOException {
        return reader.readLine();
    }

    private void handleMessage(Message message) {
        switch (message.getType().toUpperCase()) {
            case "LOGIN":
                handleLogin(message);
                break;
            case "TEXT":
                handleText(message);
                break;
            default:
                throw new IllegalArgumentException("Ukendt beskedtype: " + message.getType());
        }
    }

    private void handleLogin(Message message) {
        String requestedUsername = message.getTarget();
        if (requestedUsername.isEmpty()) {
            throw new IllegalArgumentException("Brugernavn mangler.");
        }

        if (!CLIENT_REGISTRY.isAllowedUsername(requestedUsername)) {
            sendErrorMessage(requestedUsername, "Brugernavn er ikke tilladt");
            return;
        }

        if (!CLIENT_REGISTRY.register(requestedUsername, this)) {
            sendErrorMessage(requestedUsername, "Brugernavn er allerede i brug");
            return;
        }

        username = requestedUsername;
        sendMessage(CLIENT_REGISTRY.formatTimestamp() + "|LOGIN|SERVER|" + username + "|Login godkendt");
    }

    private void handleText(Message message) {
        if (username == null) {
            sendErrorMessage(message.getTarget(), "Du skal logge ind først");
            return;
        }

        CLIENT_REGISTRY.broadcastText(username, message.getTarget(), message.getPayload());
    }

    private void sendErrorMessage(String target, String payload) {
        sendMessage(CLIENT_REGISTRY.formatTimestamp() + "|ERROR|SERVER|" + target + "|" + payload);
    }

    public void close() {
        CLIENT_REGISTRY.unregister(username, this);

        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.out.println("Kunne ikke lukke socket: " + ex.getMessage());
        }
    }
}
