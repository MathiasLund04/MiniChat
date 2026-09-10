package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient implements AutoCloseable {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private String username;
    private String currentRoom = "general";

    public ChatClient(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    public ChatClient(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom == null ? "general" : currentRoom;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
