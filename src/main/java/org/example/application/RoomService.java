package org.example.application;

import org.example.domain.ChatRoom;
import org.example.domain.MessageType;
import org.example.domain.UserSession;
import org.example.infrastructure.MessageFormatter;
import org.example.infrastructure.RoomRepository;

import java.util.stream.Collectors;
/**
 * Serviceklasse til håndtering af chatrum,
 * herunder tilslutning til rum og visning af tilgængelige rum.
 */

public class RoomService {
    private final RoomRepository roomRepository;
    private final MessageFormatter messageFormatter;

    public RoomService(RoomRepository roomRepository, MessageFormatter messageFormatter) {
        this.roomRepository = roomRepository;
        this.messageFormatter = messageFormatter;
    }

    public String joinRoom(UserSession session, String roomName) {
        ensureLoggedIn(session);

        if (roomName == null || roomName.isBlank()) {
            return messageFormatter.formatError(session.getUsername(), "Rum mangler");
        }

        roomRepository.joinRoom(session.getUsername(), roomName);
        session.setCurrentRoom(roomName);
        return messageFormatter.formatServer(MessageType.JOIN_ROOM, roomName, "Du er nu i rummet");
    }

    public String listRooms(UserSession session) {
        ensureLoggedIn(session);

        String availableRooms = roomRepository.getRooms().stream()
                .map(ChatRoom::getName)
                .collect(Collectors.joining(","));

        if (availableRooms.isBlank()) {
            return messageFormatter.formatServer(MessageType.ROOMS, "", "Ingen rum oprettet endnu");
        }

        return messageFormatter.formatServer(MessageType.ROOMS, "", availableRooms);
    }

    private void ensureLoggedIn(UserSession session) {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Du skal logge ind først");
        }
    }
}
