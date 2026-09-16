package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private static final ClientRegistry CLIENT_REGISTRY = new ClientRegistry();
    private static final ChatRoomManager CHAT_ROOM_MANAGER = new ChatRoomManager();

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final MessageParser messageParser = new MessageParser();
    private String username;
    private String currentRoom = "general";

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
                if (username == null) {
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
            case "ROOMS":
                handleRooms();
                break;
            case "JOIN_ROOM":
                handleJoinRoom(message);
                break;
            case "TEXT":
                handleText(message);
                break;
            case "PRIVATE":
                handlePrivate(message);
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
        CHAT_ROOM_MANAGER.joinRoom(username, currentRoom);
        sendMessage(CLIENT_REGISTRY.formatTimestamp() + "|LOGIN|SERVER|" + username + "|Login godkendt");
    }

    private void handleJoinRoom(Message message) {
        if (username == null) {
            sendErrorMessage("", "Du skal logge ind først");
            return;
        }

        String roomName = message.getTarget();
        if (roomName == null || roomName.isBlank()) {
            sendErrorMessage(username, "Rum mangler");
            return;
        }

        CHAT_ROOM_MANAGER.joinRoom(username, roomName);
        currentRoom = roomName;
        sendMessage(CLIENT_REGISTRY.formatTimestamp() + "|JOIN_ROOM|SERVER|" + roomName + "|Du er nu i rummet");
    }

    private void handleRooms() {
        if (username == null) {
            sendErrorMessage("", "Du skal logge ind først");
            return;
        }

        ConcurrentHashMap<String, Set<String>> rooms = CHAT_ROOM_MANAGER.getRooms();
        if (rooms.isEmpty()) {
            sendMessage(CLIENT_REGISTRY.formatTimestamp() + "|ROOMS|SERVER||Ingen rum oprettet endnu");
            return;
        }

        String availableRooms = String.join(",", rooms.keySet());
        sendMessage(CLIENT_REGISTRY.formatTimestamp() + "|ROOMS|SERVER||" + availableRooms);
    }

    private void handleText(Message message) {
        if (username == null) {
            sendErrorMessage(message.getTarget(), "Du skal logge ind først");
            return;
        }

        String roomName = message.getTarget();
        if (roomName == null || roomName.isBlank()) {
            sendErrorMessage(username, "Rum mangler");
            return;
        }

        if (!CHAT_ROOM_MANAGER.isMember(username, roomName)) {
            sendErrorMessage(roomName, "Du er ikke medlem af dette rum");
            return;
        }

        String formattedMessage = CLIENT_REGISTRY.formatTimestamp() + "|TEXT|" + username + "|" + roomName + "|" + message.getPayload();
        Set<String> members = CHAT_ROOM_MANAGER.getMembers(roomName);
        for (String member : members) {
            ClientHandler client = CLIENT_REGISTRY.getClient(member);
            if (client != null && client != this) {
                client.sendMessage(formattedMessage);
            }
        }
        sendMessage(formattedMessage);
    }

    private void handlePrivate(Message message) {
        if (username == null) {
            sendErrorMessage(message.getTarget(), "Du skal logge ind først");
            return;
        }

        String recipientName = message.getTarget();
        ClientHandler recipient = CLIENT_REGISTRY.getClient(recipientName);
        if (recipient == null) {
            sendErrorMessage(recipientName, "Modtageren findes ikke");
            return;
        }

        String privateMessage = CLIENT_REGISTRY.formatTimestamp() + "|PRIVATE|" + username + "|" + recipientName + "|" + message.getPayload();
        sendMessage(privateMessage);
        recipient.sendMessage(privateMessage);
    }

    private void sendErrorMessage(String target, String payload) {
        sendMessage(CLIENT_REGISTRY.formatTimestamp() + "|ERROR|SERVER|" + target + "|" + payload);
    }

    public void close() {
        if (username != null) {
            CHAT_ROOM_MANAGER.leaveRoom(username);
            CLIENT_REGISTRY.unregister(username, this);
        }

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
