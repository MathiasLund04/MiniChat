package org.example;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {
    private static final String DEFAULT_ROOM = "general";
    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();

    public ChatRoomManager() {
        createRoom(DEFAULT_ROOM);
    }

    public void createRoom(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            return;
        }

        rooms.computeIfAbsent(roomName, ignored -> ConcurrentHashMap.newKeySet());
    }

    public boolean joinRoom(String username, String roomName) {
        if (username == null || username.isBlank() || roomName == null || roomName.isBlank()) {
            return false;
        }

        createRoom(roomName);
        for (Set<String> members : rooms.values()) {
            members.remove(username);
        }
        rooms.get(roomName).add(username);
        return true;
    }

    public void leaveRoom(String username) {
        if (username == null || username.isBlank()) {
            return;
        }

        for (Set<String> members : rooms.values()) {
            members.remove(username);
        }
    }

    public boolean isMember(String username, String roomName) {
        if (username == null || username.isBlank() || roomName == null || roomName.isBlank()) {
            return false;
        }

        return rooms.getOrDefault(roomName, Collections.emptySet()).contains(username);
    }

    public Set<String> getMembers(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            return Collections.emptySet();
        }

        return rooms.getOrDefault(roomName, Collections.emptySet());
    }

    public ConcurrentHashMap<String, Set<String>> getRooms() {
        if (rooms == null){
            return new ConcurrentHashMap<>();
        }
        return new ConcurrentHashMap<>(rooms);
    }

}
