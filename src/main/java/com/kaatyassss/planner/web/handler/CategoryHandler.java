package com.kaatyassss.planner.web.handler;

import com.kaatyassss.planner.web.dao.CategoryDao;
import com.kaatyassss.planner.web.model.Category;
import com.kaatyassss.planner.web.model.User;
import com.kaatyassss.planner.web.session.SessionManager;
import com.kaatyassss.planner.web.util.HtmlTemplate;
import com.kaatyassss.planner.web.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class CategoryHandler implements HttpHandler {

    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        if (user == null) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("/categories".equals(path) && "GET".equalsIgnoreCase(method)) {
            HttpUtil.sendHtml(exchange, 200,
                    HtmlTemplate.categoryListPage(categoryDao.findAll(), user));

        } else if ("/categories/new".equals(path)) {
            handleNew(exchange, user);

        } else if ("/categories/edit".equals(path)) {
            handleEdit(exchange, user);

        } else if ("/categories/delete".equals(path) && "GET".equalsIgnoreCase(method)) {
            String query = exchange.getRequestURI().getQuery();
            int id = Integer.parseInt(HttpUtil.parseQuery(query).getOrDefault("id", "0"));
            categoryDao.delete(id);
            HttpUtil.redirect(exchange, "/categories");
        } else {
            HttpUtil.sendHtml(exchange, 404, "<h1>404 Not Found</h1>");
        }
    }

    private void handleNew(HttpExchange exchange, User user) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.categoryFormPage(null, user));
        } else {
            Map<String, String> form = HttpUtil.parseForm(exchange);
            String name = form.getOrDefault("name", "").trim();
            String desc = form.getOrDefault("description", "").trim();
            if (!name.isBlank()) categoryDao.create(name, desc);
            HttpUtil.redirect(exchange, "/categories");
        }
    }

    private void handleEdit(HttpExchange exchange, User user) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        int id = Integer.parseInt(HttpUtil.parseQuery(query).getOrDefault("id", "0"));
        Optional<Category> catOpt = categoryDao.findById(id);
        if (catOpt.isEmpty()) { HttpUtil.redirect(exchange, "/categories"); return; }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.categoryFormPage(catOpt.get(), user));
        } else {
            Map<String, String> form = HttpUtil.parseForm(exchange);
            String name = form.getOrDefault("name", "").trim();
            String desc = form.getOrDefault("description", "").trim();
            if (!name.isBlank()) categoryDao.update(id, name, desc);
            HttpUtil.redirect(exchange, "/categories");
        }
    }

    private User requireAuth(HttpExchange exchange) throws IOException {
        String token = SessionManager.extractToken(exchange);
        User user = SessionManager.getUser(token);
        if (user == null) { HttpUtil.redirect(exchange, "/login"); return null; }
        return user;
    }
}
