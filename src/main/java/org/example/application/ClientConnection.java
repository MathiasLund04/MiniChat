package org.example.application;
/**
 * Interface til repræsentation af en klientforbindelse, der kan sende beskeder.
 */

public interface ClientConnection {
    // Metode til at sende en besked til klienten
    void sendMessage(String message);
}
