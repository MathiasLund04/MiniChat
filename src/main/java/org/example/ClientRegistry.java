package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {
    private static final Set<String> ALLOWED_USERNAMES = Set.of("alice", "bob", "charlie");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

    private final Map<String, ClientHandler> clientsByUsername = new ConcurrentHashMap<>();

    public boolean isAllowedUsername(String username) {
        return ALLOWED_USERNAMES.contains(username);
    }

    public boolean register(String username, ClientHandler clientHandler) {
        return clientsByUsername.putIfAbsent(username, clientHandler) == null;
    }

    public void unregister(String username, ClientHandler clientHandler) {
        if (username == null) {
            return;
        }

        clientsByUsername.remove(username, clientHandler);
    }

    public ClientHandler getClient(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return clientsByUsername.get(username);
    }

    public void broadcastText(String sender, String target, String payload) {
        String formattedMessage = formatOutgoingMessage(sender, target, payload);

        for (Map.Entry<String, ClientHandler> entry : clientsByUsername.entrySet()) {
            entry.getValue().sendMessage(formattedMessage);
        }
    }

    private String formatOutgoingMessage(String sender, String target, String payload) {
        return formatTimestamp() + "|TEXT|" + sender + "|" + target + "|" + payload;
    }

    public String formatTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }
}
