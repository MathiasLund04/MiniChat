package org.example.domain;

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
