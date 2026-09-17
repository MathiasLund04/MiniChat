package org.example.infrastructure;

import org.example.application.ClientConnection;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRepository {
    private static final Set<String> ALLOWED_USERNAMES = Set.of("alice", "bob", "charlie");

    private final Map<String, ClientConnection> clientsByUsername = new ConcurrentHashMap<>();

    public boolean isAllowedUsername(String username) {
        return ALLOWED_USERNAMES.contains(username);
    }

    public boolean register(String username, ClientConnection clientConnection) {
        return clientsByUsername.putIfAbsent(username, clientConnection) == null;
    }

    public void unregister(String username, ClientConnection clientConnection) {
        if (username == null) {
            return;
        }

        clientsByUsername.remove(username, clientConnection);
    }

    public ClientConnection getClient(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return clientsByUsername.get(username);
    }
}
