package org.example;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {
    private final Map<String, ChatClient> clients = new ConcurrentHashMap<>();

    public synchronized boolean register(String username, ChatClient client) {
        if (username == null || username.isBlank() || client == null) {
            return false;
        }

        if (clients.containsKey(username)) {
            return false;
        }

        clients.put(username, client);
        client.setUsername(username);
        return true;
    }

    public synchronized boolean unregister(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return clients.remove(username) != null;
    }

    public synchronized void unregister(ChatClient client) {
        if (client == null) {
            return;
        }

        clients.entrySet().removeIf(entry -> entry.getValue() == client);
    }

    public synchronized boolean contains(String username) {
        return username != null && clients.containsKey(username);
    }

    public synchronized ChatClient get(String username) {
        if (username == null) {
            return null;
        }
        return clients.get(username);
    }

    public synchronized Collection<ChatClient> all() {
        return Collections.unmodifiableCollection(clients.values());
    }

    public synchronized Set<String> usernames() {
        return Collections.unmodifiableSet(clients.keySet());
    }
}
