package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
        this.writer = new PrintWriter(
                new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                true
        );
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Modtaget fra klient: " + message);
                sendMessage("Ekko: " + message);
            }

            System.out.println("Klienten lukker forbindelsen.");
        } catch (IOException ex) {
            System.out.println("Fejl i ClientHandler: " + ex.getMessage());
        } finally {
            close();
        }
    }

    public void sendMessage(String message) {
        if (message == null) {
            return;
        }
        writer.println(message);
        writer.flush();
    }

    public String readMessage() throws IOException {
        return reader.readLine();
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.out.println("Kunne ikke lukke socket: " + ex.getMessage());
        }
    }
}
