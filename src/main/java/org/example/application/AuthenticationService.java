package org.example.application;

import org.example.domain.MessageType;
import org.example.domain.UserSession;
import org.example.infrastructure.ClientRepository;
import org.example.infrastructure.MessageFormatter;
import org.example.infrastructure.RoomRepository;

public class AuthenticationService {
    private final ClientRepository clientRepository;
    private final RoomRepository roomRepository;
    private final MessageFormatter messageFormatter;

    public AuthenticationService(ClientRepository clientRepository, RoomRepository roomRepository, MessageFormatter messageFormatter) {
        this.clientRepository = clientRepository;
        this.roomRepository = roomRepository;
        this.messageFormatter = messageFormatter;
    }

    public String login(String requestedUsername, UserSession session, ClientConnection connection) {
        if (requestedUsername == null || requestedUsername.isBlank()) {
            throw new IllegalArgumentException("Brugernavn mangler.");
        }

        if (!clientRepository.isAllowedUsername(requestedUsername)) {
            return messageFormatter.formatError(requestedUsername, "Brugernavn er ikke tilladt");
        }

        if (!clientRepository.register(requestedUsername, connection)) {
            return messageFormatter.formatError(requestedUsername, "Brugernavn er allerede i brug");
        }

        session.setUsername(requestedUsername);
        roomRepository.joinRoom(requestedUsername, session.getCurrentRoom());
        return messageFormatter.formatServer(MessageType.LOGIN, requestedUsername, "Login godkendt");
    }
}
