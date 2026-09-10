package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements AutoCloseable {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ClientRegistry clientRegistry = new ClientRegistry();
    private final ChatRoomManager chatRoomManager = new ChatRoomManager();
    private volatile boolean running = true;

    public ChatServer() throws IOException {
        this(8080);
    }

    public ChatServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ClientRegistry getClientRegistry() {
        return clientRegistry;
    }

    public ChatRoomManager getChatRoomManager() {
        return chatRoomManager;
    }

    public void start() {
        executorService.submit(() -> {
            while (running && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(new ClientHandler(this, clientSocket));
                } catch (IOException ex) {
                    if (running) {
                        IO.println("Server error: " + ex.getMessage());
                    }
                }
            }
        });
    }

    public void registerClient(ChatClient client) {
        if (client == null) {
            return;
        }

        String username = client.getUsername();
        if (username == null || username.isBlank()) {
            username = "user-" + System.identityHashCode(client);
            client.setUsername(username);
        }

        clientRegistry.register(username, client);
        chatRoomManager.createRoom("general");
        chatRoomManager.joinRoom("general", client);
    }

    public void removeClient(Socket socket) {
        if (socket == null) {
            return;
        }

        for (ChatClient client : clientRegistry.all()) {
            if (client.getSocket() == socket) {
                clientRegistry.unregister(client.getUsername());
                chatRoomManager.removeClient(client);
                return;
            }
        }
    }

    public void broadcast(String message) {
        for (ChatClient client : clientRegistry.all()) {
            client.sendMessage(message);
        }
    }

    public void handleIncomingMessage(ChatClient client, Message message) {
        if (client == null || message == null) {
            return;
        }

        String command = message.getCommand();
        if (command == null || command.isBlank()) {
            client.sendMessage("ERROR|server|Unknown command");
            return;
        }

        String normalizedCommand = command.toUpperCase(Locale.ROOT);
        switch (normalizedCommand) {
            case "JOIN_ROOM":
                String roomName = message.getContent();
                String room = roomName == null || roomName.isBlank() ? "general" : roomName.trim();
                chatRoomManager.createRoom(room);
                chatRoomManager.joinRoom(room, client);
                client.setCurrentRoom(room);
                client.sendMessage("OK|server|Joined room " + room);
                break;
            case "TEXT":
                String sender = client.getUsername();
                String payload = message.getContent();
                String text = payload == null ? "" : payload;
                String protocol = "TEXT|" + sender + "|" + client.getCurrentRoom() + "|" + text;
                broadcast(protocol);
                break;
            default:
                client.sendMessage("ERROR|server|Unknown command");
                break;
        }
    }

    public void shutdown() throws IOException {
        running = false;
        serverSocket.close();
        executorService.shutdownNow();
    }

    @Override
    public void close() throws IOException {
        shutdown();
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer(8080);
            server.start();
            IO.println("ChatServer started on port " + server.getPort());
        } catch (IOException ex) {
            IO.println("Could not start ChatServer: " + ex.getMessage());
        }
    }
}
