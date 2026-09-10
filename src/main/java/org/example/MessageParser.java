package org.example;

import java.util.Objects;

public final class MessageParser {
    public static final String FIELD_SEPARATOR = "\u001F";

    private MessageParser() {
    }

    public static Message parse(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return null;
        }

        String text = rawMessage.trim();
        String[] parts = text.split(FIELD_SEPARATOR, -1);
        if (parts.length == 0) {
            return null;
        }

        Message message = new Message();
        message.setCommand(parts[0].trim());
        if (parts.length > 1) {
            message.setSender(parts[1].trim());
        }
        if (parts.length > 2) {
            message.setTarget(parts[2].trim());
        }
        if (parts.length > 3) {
            message.setContent(parts[3].trim());
        }

        if (parts.length > 4) {
            StringBuilder content = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                if (i > 3) {
                    content.append(FIELD_SEPARATOR);
                }
                content.append(parts[i]);
            }
            message.setContent(content.toString());
        }

        return message;
    }

    public static String format(Message message) {
        if (message == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(Objects.toString(message.getCommand(), ""));
        builder.append(FIELD_SEPARATOR);
        builder.append(Objects.toString(message.getSender(), ""));
        builder.append(FIELD_SEPARATOR);
        builder.append(Objects.toString(message.getTarget(), ""));
        builder.append(FIELD_SEPARATOR);
        builder.append(Objects.toString(message.getContent(), ""));
        return builder.toString();
    }
}
