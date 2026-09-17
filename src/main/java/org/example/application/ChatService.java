package org.example.application;

import org.example.domain.MessageType;
import org.example.domain.UserSession;
import org.example.infrastructure.ClientRepository;
import org.example.infrastructure.MessageFormatter;
import org.example.infrastructure.RoomRepository;

import java.util.Set;

public class ChatService {
    private final ClientRepository clientRepository;
    private final RoomRepository roomRepository;
    private final MessageFormatter messageFormatter;

    public ChatService(ClientRepository clientRepository, RoomRepository roomRepository, MessageFormatter messageFormatter) {
        this.clientRepository = clientRepository;
        this.roomRepository = roomRepository;
        this.messageFormatter = messageFormatter;
    }

    public String sendRoomMessage(UserSession session, String roomName, String payload, ClientConnection senderConnection) {
        ensureLoggedIn(session);

        if (roomName == null || roomName.isBlank()) {
            return messageFormatter.formatError(session.getUsername(), "Rum mangler");
        }

        if (!roomRepository.isMember(session.getUsername(), roomName)) {
            return messageFormatter.formatError(roomName, "Du er ikke medlem af dette rum");
        }

        String formattedMessage = messageFormatter.format(MessageType.TEXT, session.getUsername(), roomName, payload);
        Set<String> members = roomRepository.getMembers(roomName);
        for (String member : members) {
            ClientConnection connection = clientRepository.getClient(member);
            if (connection != null && connection != senderConnection) {
                connection.sendMessage(formattedMessage);
            }
        }
        return formattedMessage;
    }

    public String sendPrivateMessage(UserSession session, String recipientName, String payload, ClientConnection senderConnection) {
        ensureLoggedIn(session);

        ClientConnection recipient = clientRepository.getClient(recipientName);
        if (recipient == null) {
            return messageFormatter.formatError(recipientName, "Modtageren findes ikke");
        }

        String privateMessage = messageFormatter.format(MessageType.PRIVATE, session.getUsername(), recipientName, payload);
        if (recipient != senderConnection) {
            recipient.sendMessage(privateMessage);
        }
        return privateMessage;
    }

    public String quit(UserSession session) {
        return messageFormatter.formatServer(MessageType.QUIT, session.getUsername() == null ? "" : session.getUsername(), "Forbindelsen lukkes");
    }

    private void ensureLoggedIn(UserSession session) {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Du skal logge ind først");
        }
    }
}
