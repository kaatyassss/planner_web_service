package com.kaatyassss.planner.web.handler;

import com.kaatyassss.planner.web.dao.UserDao;
import com.kaatyassss.planner.web.model.User;
import com.kaatyassss.planner.web.session.SessionManager;
import com.kaatyassss.planner.web.util.HtmlTemplate;
import com.kaatyassss.planner.web.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class LoginHandler implements HttpHandler {

    private final UserDao userDao = new UserDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.loginPage(null));
            return;
        }

        if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> form = HttpUtil.parseForm(exchange);
            String username = form.getOrDefault("username", "").trim();
            String password = form.getOrDefault("password", "");

            Optional<User> userOpt = userDao.findByUsername(username);
            if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
                String token = SessionManager.createSession(userOpt.get());
                exchange.getResponseHeaders().add("Set-Cookie",
                        SessionManager.getCookieName() + "=" + token + "; Path=/; HttpOnly");
                HttpUtil.redirect(exchange, "/");
            } else {
                HttpUtil.sendHtml(exchange, 200, HtmlTemplate.loginPage("Неверный логин или пароль"));
            }
        }
    }
}
