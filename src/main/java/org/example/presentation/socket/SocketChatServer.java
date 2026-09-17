package org.example.presentation.socket;

import org.example.application.AuthenticationService;
import org.example.application.ChatCommandDispatcher;
import org.example.application.ChatService;
import org.example.application.RoomService;
import org.example.infrastructure.ClientRepository;
import org.example.infrastructure.MessageFormatter;
import org.example.infrastructure.MessageParser;
import org.example.infrastructure.RoomRepository;
import org.example.infrastructure.TimestampProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SocketChatServer er en simpel chatserver, der accepterer klientforbindelser via sockets.
 * Den håndterer klientforbindelser i separate tråde og bruger en dispatcher til at behandle beskeder.
 */

public class SocketChatServer {
    private final int port;
    private final ExecutorService executorService;
    private final ClientRepository clientRepository;
    private final RoomRepository roomRepository;
    private final MessageParser messageParser;
    private final ChatCommandDispatcher dispatcher;
    private final String defaultRoom;

    public SocketChatServer(
            int port,
            ExecutorService executorService,
            ClientRepository clientRepository,
            RoomRepository roomRepository,
            MessageParser messageParser,
            ChatCommandDispatcher dispatcher,
            String defaultRoom
    ) {
        this.port = port;
        this.executorService = executorService;
        this.clientRepository = clientRepository;
        this.roomRepository = roomRepository;
        this.messageParser = messageParser;
        this.dispatcher = dispatcher;
        this.defaultRoom = defaultRoom;
    }
    public static SocketChatServer createDefault(int port, ExecutorService executorService) {
        ClientRepository clientRepository = new ClientRepository();
        RoomRepository roomRepository = new RoomRepository();
        roomRepository.createRoom("general");
        TimestampProvider timestampProvider = new TimestampProvider();
        MessageFormatter messageFormatter = new MessageFormatter(timestampProvider);
        MessageParser messageParser = new MessageParser();
        AuthenticationService authenticationService = new AuthenticationService(clientRepository, roomRepository, messageFormatter);
        RoomService roomService = new RoomService(roomRepository, messageFormatter);
        ChatService chatService = new ChatService(clientRepository, roomRepository, messageFormatter);
        ChatCommandDispatcher dispatcher = new ChatCommandDispatcher(authenticationService, roomService, chatService, messageFormatter);
        return new SocketChatServer(port, executorService, clientRepository, roomRepository, messageParser, dispatcher, "general");
    }
     /**
     * Starter serveren og accepterer klientforbindelser.
     * Hver klientforbindelse håndteres i en separat tråd.
     */
    public void start() {
        System.out.println("Server startet");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Venter på klienter");

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Klient forbundet: " + clientSocket.getRemoteSocketAddress());
                    executorService.submit(new ClientConnectionHandler(
                            clientSocket,
                            messageParser,
                            dispatcher,
                            clientRepository,
                            roomRepository,
                            defaultRoom
                    ));
                } catch (IOException exception) {
                    System.out.println("Fejl i serverforbindelse: " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            System.out.println("Fejl i server: " + exception.getMessage());
        } finally {
            shutdownExecutor();
        }
    }
    /**
     * Lukker ExecutorService korrekt, venter på afslutning af aktive tråde.
     * Hvis trådene ikke afsluttes inden for 5 sekunder, forsøger den at lukke dem ned med shutdownNow().
     */

    private void shutdownExecutor() {
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("ExecutorService kunne ikke lukkes korrekt.");
                }
            }
        } catch (InterruptedException exception) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("Shutdown af ExecutorService blev afbrudt.");
        }
    }
}
