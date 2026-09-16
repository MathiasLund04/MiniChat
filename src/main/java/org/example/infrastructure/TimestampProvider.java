package org.example.infrastructure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampProvider {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

    public String now() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }
}
