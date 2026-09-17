package org.example.application;

import org.example.domain.Message;
import org.example.domain.MessageType;
import org.example.domain.UserSession;
import org.example.infrastructure.MessageFormatter;

public class ChatCommandDispatcher {
    private final AuthenticationService authenticationService;
    private final RoomService roomService;
    private final ChatService chatService;
    private final MessageFormatter messageFormatter;

    public ChatCommandDispatcher(
            AuthenticationService authenticationService,
            RoomService roomService,
            ChatService chatService,
            MessageFormatter messageFormatter
    ) {
        this.authenticationService = authenticationService;
        this.roomService = roomService;
        this.chatService = chatService;
        this.messageFormatter = messageFormatter;
    }

    public String dispatch(Message message, UserSession session, ClientConnection connection) {
        try {
            switch (message.getType()) {
                case LOGIN:
                    return authenticationService.login(message.getTarget(), session, connection);
                case ROOMS:
                    return roomService.listRooms(session);
                case JOIN_ROOM:
                    return roomService.joinRoom(session, message.getTarget());
                case TEXT:
                    return chatService.sendRoomMessage(session, message.getTarget(), message.getPayload(), connection);
                case PRIVATE:
                    return chatService.sendPrivateMessage(session, message.getTarget(), message.getPayload(), connection);
                case QUIT:
                    return chatService.quit(session);
                case ERROR:
return messageFormatter.formatError(message.getTarget(), message.getPayload());
                default:
                    throw new IllegalArgumentException("Ukendt beskedtype: " + message.getType());
            }
        } catch (IllegalStateException | IllegalArgumentException exception) {
            String target = message.getTarget() == null ? "" : message.getTarget();
            return messageFormatter.formatError(target, exception.getMessage());
        }
    }
}
