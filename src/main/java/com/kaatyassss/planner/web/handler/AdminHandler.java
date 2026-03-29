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

public class AdminHandler implements HttpHandler {

    private final UserDao userDao = new UserDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        User user = requireAdmin(exchange);
        if (user == null) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("/admin".equals(path) && "GET".equalsIgnoreCase(method)) {
            HttpUtil.sendHtml(exchange, 200,
                    HtmlTemplate.adminPage(userDao.findAll(), user));

        } else if ("/admin/new".equals(path)) {
            handleNew(exchange, user);

        } else if ("/admin/edit".equals(path)) {
            handleEdit(exchange, user);

        } else if ("/admin/delete".equals(path) && "GET".equalsIgnoreCase(method)) {
            String query = exchange.getRequestURI().getQuery();
            int id = Integer.parseInt(HttpUtil.parseQuery(query).getOrDefault("id", "0"));
            if (id != user.getId()) userDao.delete(id);
            HttpUtil.redirect(exchange, "/admin");
        } else {
            HttpUtil.sendHtml(exchange, 404, "<h1>404 Not Found</h1>");
        }
    }

    private void handleNew(HttpExchange exchange, User currentUser) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.userFormPage(null, currentUser));
        } else {
            Map<String, String> form = HttpUtil.parseForm(exchange);
            String username = form.getOrDefault("username", "").trim();
            String email    = form.getOrDefault("email", "").trim();
            String password = form.getOrDefault("password", "").trim();
            String role     = form.getOrDefault("role", "user");
            if (!username.isBlank() && !password.isBlank()) {
                userDao.create(username, password, email, role);
            }
            HttpUtil.redirect(exchange, "/admin");
        }
    }

    private void handleEdit(HttpExchange exchange, User currentUser) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        int id = Integer.parseInt(HttpUtil.parseQuery(query).getOrDefault("id", "0"));
        Optional<User> editUserOpt = userDao.findById(id);
        if (editUserOpt.isEmpty()) { HttpUtil.redirect(exchange, "/admin"); return; }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.userFormPage(editUserOpt.get(), currentUser));
        } else {
            Map<String, String> form = HttpUtil.parseForm(exchange);
            String username = form.getOrDefault("username", "").trim();
            String email    = form.getOrDefault("email", "").trim();
            String role     = form.getOrDefault("role", "user");
            String password = form.getOrDefault("password", "").trim();

            if (!username.isBlank()) {
                userDao.update(id, username, email, role);
                if (!password.isBlank()) {
                    userDao.updatePassword(id, password);
                }
            }
            HttpUtil.redirect(exchange, "/admin");
        }
    }

    private User requireAdmin(HttpExchange exchange) throws IOException {
        String token = SessionManager.extractToken(exchange);
        User user = SessionManager.getUser(token);
        if (user == null) { HttpUtil.redirect(exchange, "/login"); return null; }
        if (!user.isAdmin()) { HttpUtil.redirect(exchange, "/"); return null; }
        return user;
    }
}
