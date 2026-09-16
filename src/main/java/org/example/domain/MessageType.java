package org.example.domain;

public enum MessageType {
    LOGIN,
    ROOMS,
    JOIN_ROOM,
    TEXT,
    PRIVATE,
    QUIT,
    ERROR;

    public static MessageType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Beskedtype mangler.");
        }

        try {
            return MessageType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Ukendt beskedtype: " + value);
        }
    }
}
