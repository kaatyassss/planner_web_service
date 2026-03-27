package com.kaatyassss.planner.web.session;

import com.kaatyassss.planner.web.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {

    private static final Map<String, User> SESSIONS = new ConcurrentHashMap<>();
    private static final String COOKIE_NAME = "PLANNER_SESSION";

    private SessionManager() {}

    public static String createSession(User user) {
        String token = UUID.randomUUID().toString();
        SESSIONS.put(token, user);
        return token;
    }

    public static User getUser(String token) {
        if (token == null) return null;
        return SESSIONS.get(token);
    }

    public static void invalidate(String token) {
        if (token != null) SESSIONS.remove(token);
    }

    public static String getCookieName() {
        return COOKIE_NAME;
    }

    public static String extractToken(com.sun.net.httpserver.HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Cookie");
        if (header == null) return null;
        for (String part : header.split(";")) {
            part = part.trim();
            if (part.startsWith(COOKIE_NAME + "=")) {
                return part.substring(COOKIE_NAME.length() + 1).trim();
            }
        }
        return null;
    }
}
