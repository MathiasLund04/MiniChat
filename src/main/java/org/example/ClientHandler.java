package org.example;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ChatServer server;
    private final Socket clientSocket;

    public ClientHandler(ChatServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (ChatClient client = new ChatClient(clientSocket)) {
            server.registerClient(client);
            String line;
            while ((line = client.readMessage()) != null) {
                Message message = MessageParser.parse(line);
                if (message == null) {
                    client.sendMessage("ERROR|server|Bad message");
                    continue;
                }
                server.handleIncomingMessage(client, message);
            }
        } catch (IOException ex) {
            server.removeClient(clientSocket);
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
