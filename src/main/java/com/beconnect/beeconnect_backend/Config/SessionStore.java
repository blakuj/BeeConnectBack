package com.beconnect.beeconnect_backend.Config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStore {
    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public void storeSession(String sessionId, String email) {
        sessions.put(sessionId, email);
    }

    public String getEmail(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}