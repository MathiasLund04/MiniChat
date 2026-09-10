package org.example;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {
    private final Map<String, Set<ChatClient>> rooms = new ConcurrentHashMap<>();

    public synchronized void createRoom(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            return;
        }
        rooms.computeIfAbsent(roomName.trim(), ignored -> new HashSet<>());
    }

    public synchronized boolean roomExists(String roomName) {
        return roomName != null && rooms.containsKey(roomName.trim());
    }

    public synchronized boolean joinRoom(String roomName, ChatClient client) {
        if (client == null || roomName == null || roomName.isBlank()) {
            return false;
        }

        String normalizedRoom = roomName.trim();
        createRoom(normalizedRoom);
        return rooms.get(normalizedRoom).add(client);
    }

    public synchronized boolean leaveRoom(String roomName, ChatClient client) {
        if (roomName == null || roomName.isBlank() || client == null) {
            return false;
        }

        Set<ChatClient> members = rooms.get(roomName.trim());
        if (members == null) {
            return false;
        }

        boolean removed = members.remove(client);
        if (members.isEmpty()) {
            rooms.remove(roomName.trim());
        }
        return removed;
    }

    public synchronized Set<ChatClient> getMembers(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            return Collections.emptySet();
        }

        Set<ChatClient> members = rooms.get(roomName.trim());
        return members == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(members));
    }

    public synchronized void removeClient(ChatClient client) {
        if (client == null) {
            return;
        }

        rooms.values().forEach(members -> members.remove(client));
        rooms.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
