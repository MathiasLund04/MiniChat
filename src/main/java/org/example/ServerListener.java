package org.example;

import java.io.IOException;
import java.util.function.Consumer;

public class ServerListener implements Runnable {
    private final ChatClient chatClient;
    private final Consumer<String> messageHandler;

    public ServerListener(ChatClient chatClient, Consumer<String> messageHandler) {
        this.chatClient = chatClient;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = chatClient.readMessage()) != null) {
                if (messageHandler != null) {
                    messageHandler.accept(line);
                }
            }
        } catch (IOException ex) {
            if (messageHandler != null) {
                messageHandler.accept("ERROR|server|Connection closed");
            }
        }
    }
}
