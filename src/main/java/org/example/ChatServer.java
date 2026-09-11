package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChatServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 10;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);

        System.out.println("Server startet");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Venter på klienter");

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Klient forbundet: " + clientSocket.getRemoteSocketAddress());
                    executorService.submit(new ClientHandler(clientSocket));
                } catch (IOException ex) {
                    System.out.println("Fejl i serverforbindelse: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.println("Fejl i server: " + ex.getMessage());
        } finally {
            shutdownExecutor(executorService);
        }
    }

    private static void shutdownExecutor(ExecutorService executorService) {
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("ExecutorService kunne ikke lukkes korrekt.");
                }
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("Shutdown af ExecutorService blev afbrudt.");
        }
    }
}
