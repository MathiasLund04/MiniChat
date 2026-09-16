package org.example.infrastructure;

import org.example.domain.Message;
import org.example.domain.MessageType;

public class MessageParser {
    public Message parse(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Beskeden må ikke være tom.");
        }

        String[] fields = message.split("\\|", 3);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Ugyldigt beskedformat. Forventede TYPE|TARGET|PAYLOAD.");
        }

        MessageType type = MessageType.from(fields[0]);
        String target = fields[1].trim();
        String payload = fields[2].trim();

        return new Message(type, target, payload);
    }
}
