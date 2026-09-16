package org.example.domain;

public class UserSession {
    private String username;
    private String currentRoom;

    public UserSession(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLoggedIn() {
        return username != null;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }
}
