package org.example.presentation.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
/**
 * SocketChatClient er en simpel klient, der kan oprette forbindelse til en chatserver via sockets.
 * Den kan sende beskeder til serveren og modtage svar.
 */

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
        String response = reader.readLine();
        if (response == null) {
            throw new IOException("Serveren afbrød forbindelsen uden at sende et svar.");
        }
        return response;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void send(String request) {
        writer.println(request);
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
