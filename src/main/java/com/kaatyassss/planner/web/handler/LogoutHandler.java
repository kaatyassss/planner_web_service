package com.kaatyassss.planner.web.handler;

import com.kaatyassss.planner.web.session.SessionManager;
import com.kaatyassss.planner.web.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LogoutHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String token = SessionManager.extractToken(exchange);
        SessionManager.invalidate(token);
        exchange.getResponseHeaders().add("Set-Cookie",
                SessionManager.getCookieName() + "=; Path=/; Max-Age=0");
        HttpUtil.redirect(exchange, "/login");
    }
}
