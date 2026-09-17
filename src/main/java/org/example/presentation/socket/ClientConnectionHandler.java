package org.example.presentation.socket;

import org.example.application.ChatCommandDispatcher;
import org.example.application.ClientConnection;
import org.example.domain.Message;
import org.example.domain.MessageType;
import org.example.domain.UserSession;
import org.example.infrastructure.ClientRepository;
import org.example.infrastructure.MessageParser;
import org.example.infrastructure.RoomRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnectionHandler implements Runnable, ClientConnection {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final MessageParser messageParser;
    private final ChatCommandDispatcher dispatcher;
    private final ClientRepository clientRepository;
    private final RoomRepository roomRepository;
    private final UserSession session;
    private boolean running = true;

    public ClientConnectionHandler(
            Socket socket,
            MessageParser messageParser,
            ChatCommandDispatcher dispatcher,
            ClientRepository clientRepository,
            RoomRepository roomRepository,
            String defaultRoom
    ) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        this.messageParser = messageParser;
        this.dispatcher = dispatcher;
        this.clientRepository = clientRepository;
        this.roomRepository = roomRepository;
        this.session = new UserSession(defaultRoom);
    }

    @Override
    public void run() {
        try {
            String rawMessage;
            while (running && (rawMessage = reader.readLine()) != null) {
                logIncoming(rawMessage);
                try {
                    Message message = messageParser.parse(rawMessage);
                    String response = dispatcher.dispatch(message, session, this);
                    sendMessage(response);
                    if (message.getType() == MessageType.QUIT) {
                        running = false;
                    }
                } catch (IllegalArgumentException exception) {
                    sendMessage(dispatcher.dispatch(
                            new Message(MessageType.ERROR, "", exception.getMessage()),
                            session,
                            this
                    ));
                }
            }

            if (running) {
                System.out.println("Klienten lukker forbindelsen.");
            }
        } catch (IOException exception) {
            System.out.println("Fejl i ClientConnectionHandler: " + exception.getMessage());
        } finally {
            close();
        }
    }

    @Override
    public synchronized void sendMessage(String message) {
        if (message == null) {
            return;
        }

        writer.println(message);
        writer.flush();
    }

    private void logIncoming(String rawMessage) {
        if (session.getUsername() == null) {
            System.out.println("Modtaget fra klient: " + rawMessage);
        } else {
            System.out.println("Modtaget fra " + session.getUsername() + ": " + rawMessage);
        }
    }

    public void close() {
        if (session.getUsername() != null) {
            roomRepository.leaveRoom(session.getUsername());
            clientRepository.unregister(session.getUsername(), this);
        }

        try {
            writer.close();
            reader.close();
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException exception) {
            System.out.println("Kunne ikke lukke socket: " + exception.getMessage());
        }
    }
}
