package org.example.domain;

/**
 * Representerer en besked med en type, et target og en payload(Beskeden).
 * Denne klasse er uforanderlig (immutable) og trådsikker.
 */

public class Message {
    private final MessageType type;
    private final String target;
    private final String payload;

    public Message(MessageType type, String target, String payload) {
        this.type = type;
        this.target = target;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getPayload() {
        return payload;
    }
}
