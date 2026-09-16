package org.example.presentation.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketChatClient implements AutoCloseable {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public SocketChatClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    }

    public String sendAndRead(String request) throws IOException {
        writer.println(request);
        writer.flush();
        System.out.println(request);

        String response = reader.readLine();
        if (response == null) {
            throw new IOException("Serveren afbrød forbindelsen uden at sende et svar.");
        }

        System.out.println("Svar: " + response);
        return response;
    }

    public BufferedReader reader() {
        return reader;
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
