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

public class RegisterHandler implements HttpHandler {

    private final UserDao userDao = new UserDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.registerPage(null));
            return;
        }

        Map<String, String> form = HttpUtil.parseForm(exchange);
        String username = form.getOrDefault("username", "").trim();
        String email    = form.getOrDefault("email", "").trim();
        String password = form.getOrDefault("password", "").trim();
        String confirm  = form.getOrDefault("confirm", "").trim();

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.registerPage("Заполните все поля"));
            return;
        }
        if (!password.equals(confirm)) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.registerPage("Пароли не совпадают"));
            return;
        }
        if (userDao.findByUsername(username).isPresent()) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.registerPage("Пользователь с таким логином уже существует"));
            return;
        }

        User user = userDao.create(username, password, email, "user");
        String token = SessionManager.createSession(user);
        exchange.getResponseHeaders().add("Set-Cookie",
                SessionManager.getCookieName() + "=" + token + "; Path=/; HttpOnly");
        HttpUtil.redirect(exchange, "/");
    }
}
