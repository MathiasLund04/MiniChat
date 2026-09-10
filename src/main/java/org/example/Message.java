package org.example;

public class Message {
    private String command;
    private String sender;
    private String target;
    private String content;
    private long timestamp;

    public Message() {
        this("", "", "", "");
    }

    public Message(String command, String sender, String content) {
        this(command, sender, "", content);
    }

    public Message(String command, String sender, String target, String content) {
        this.command = command == null ? "" : command;
        this.sender = sender == null ? "" : sender;
        this.target = target == null ? "" : target;
        this.content = content == null ? "" : content;
        this.timestamp = System.currentTimeMillis();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command == null ? "" : command;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender == null ? "" : sender;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target == null ? "" : target;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? "" : content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String toProtocol() {
        return MessageParser.format(this);
    }

    @Override
    public String toString() {
        return "Message{" +
                "command='" + command + '\'' +
                ", sender='" + sender + '\'' +
                ", target='" + target + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
