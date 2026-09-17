package org.example.infrastructure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * TimestampProvider er en klasse, der leverer den aktuelle tidsstempel i et bestemt format.
 * Den bruger Java's DateTimeFormatter til at formatere LocalDateTime til en streng.
 */

public class TimestampProvider {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

    public String now() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }
}
