package org.example.domain;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {
    private final String name;
    private final Set<String> members = ConcurrentHashMap.newKeySet();

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addMember(String username) {
        members.add(username);
    }

    public void removeMember(String username) {
        members.remove(username);
    }

    public boolean hasMember(String username) {
        return members.contains(username);
    }

    public Set<String> getMembers() {
        return Collections.unmodifiableSet(members);
    }
}
