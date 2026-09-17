package org.example.app;

import org.example.presentation.socket.SocketChatServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApplication {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 10;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);
        SocketChatServer server = SocketChatServer.createDefault(PORT, executorService);
        server.start();
    }
}
