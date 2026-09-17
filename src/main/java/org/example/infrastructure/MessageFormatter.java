package org.example.infrastructure;

import org.example.domain.MessageType;

public class MessageFormatter {
    private static final String SERVER = "SERVER";

    private final TimestampProvider timestampProvider;

    public MessageFormatter(TimestampProvider timestampProvider) {
        this.timestampProvider = timestampProvider;
    }

    public String format(MessageType type, String sender, String target, String payload) {
        return timestampProvider.now()
                + "|" + type.name()
                + "|" + sender
                + "|" + target
                + "|" + payload;
    }

    public String formatServer(MessageType type, String target, String payload) {
        return format(type, SERVER, target, payload);
    }

    public String formatError(String target, String payload) {
        return formatServer(MessageType.ERROR, target, payload);
    }
}
