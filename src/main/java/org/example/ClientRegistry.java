package org.example;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {
    private static final Set<String> ALLOWED_USERNAMES = Set.of("alice", "bob", "charlie");

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

    public void broadcastText(String sender, String target, String payload) {
        String formattedMessage = formatOutgoingMessage(sender, target, payload);

        for (Map.Entry<String, ClientHandler> entry : clientsByUsername.entrySet()) {
            entry.getValue().sendMessage(formattedMessage);
        }
    }

    private String formatOutgoingMessage(String sender, String target, String payload) {
        return System.currentTimeMillis() + "|TEXT|" + sender + "|" + target + "|" + payload;
    }
}
