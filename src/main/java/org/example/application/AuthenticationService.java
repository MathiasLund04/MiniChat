package org.example.application;

import org.example.domain.MessageType;
import org.example.domain.UserSession;
import org.example.infrastructure.ClientRepository;
import org.example.infrastructure.MessageFormatter;
import org.example.infrastructure.RoomRepository;

    // Serviceklasse til håndtering af autentificering og login af brugere

public class AuthenticationService {
    private final ClientRepository clientRepository;
    private final RoomRepository roomRepository;
    private final MessageFormatter messageFormatter;

    public AuthenticationService(ClientRepository clientRepository, RoomRepository roomRepository, MessageFormatter messageFormatter) {
        this.clientRepository = clientRepository;
        this.roomRepository = roomRepository;
        this.messageFormatter = messageFormatter;
    }

    // Metode til at håndtere login af brugere
    public String login(String requestedUsername, UserSession session, ClientConnection connection) {
        if (requestedUsername == null || requestedUsername.isBlank()) {
            throw new IllegalArgumentException("Brugernavn mangler.");
        }

        if (!clientRepository.isAllowedUsername(requestedUsername)) {
            return messageFormatter.formatError(requestedUsername, "Brugernavn er ikke tilladt");
        }

if (session.isLoggedIn()) {
            return messageFormatter.formatError(requestedUsername, "Du er allerede logget ind");
        }

        if (!clientRepository.register(requestedUsername, connection)) {
            return messageFormatter.formatError(requestedUsername, "Brugernavn er allerede i brug");
        }

        session.setUsername(requestedUsername);
        roomRepository.joinRoom(requestedUsername, session.getCurrentRoom());
        return messageFormatter.formatServer(MessageType.LOGIN, requestedUsername, "Login godkendt");
    }
}
