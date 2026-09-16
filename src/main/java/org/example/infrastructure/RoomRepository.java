package org.example.infrastructure;

import org.example.domain.ChatRoom;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RoomRepository {
    private final Map<String, ChatRoom> rooms = new ConcurrentHashMap<>();

    public void createRoom(String roomName) {
        if (roomName == null || roomName.isBlank()) {
            return;
        }

        rooms.computeIfAbsent(roomName, ChatRoom::new);
    }

    public boolean joinRoom(String username, String roomName) {
        if (username == null || username.isBlank() || roomName == null || roomName.isBlank()) {
            return false;
        }

        createRoom(roomName);
        for (ChatRoom room : rooms.values()) {
            room.removeMember(username);
        }
        rooms.get(roomName).addMember(username);
        return true;
    }

    public void leaveRoom(String username) {
        if (username == null || username.isBlank()) {
            return;
        }

        for (ChatRoom room : rooms.values()) {
            room.removeMember(username);
        }
    }

    public boolean isMember(String username, String roomName) {
        if (username == null || username.isBlank() || roomName == null || roomName.isBlank()) {
            return false;
        }

        ChatRoom room = rooms.get(roomName);
        return room != null && room.hasMember(username);
    }

    public Set<String> getMembers(String roomName) {
        ChatRoom room = rooms.get(roomName);
        if (room == null) {
            return Collections.emptySet();
        }

        return room.getMembers();
    }

    public Collection<ChatRoom> getRooms() {
        return Collections.unmodifiableCollection(rooms.values());
    }
}
