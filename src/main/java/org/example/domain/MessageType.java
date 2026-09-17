package org.example.domain;
/**
 * Enum for at repræsentere forskellige typer af beskeder i chatapplikationen.
 * Indeholder metoder til at konvertere fra streng til enum type.
 */

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
