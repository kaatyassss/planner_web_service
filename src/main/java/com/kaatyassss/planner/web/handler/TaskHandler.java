package com.kaatyassss.planner.web.handler;

import com.kaatyassss.planner.web.dao.CategoryDao;
import com.kaatyassss.planner.web.dao.TaskDao;
import com.kaatyassss.planner.web.model.Category;
import com.kaatyassss.planner.web.model.Task;
import com.kaatyassss.planner.web.model.User;
import com.kaatyassss.planner.web.session.SessionManager;
import com.kaatyassss.planner.web.util.HtmlTemplate;
import com.kaatyassss.planner.web.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskHandler implements HttpHandler {

    private final TaskDao taskDao = new TaskDao();
    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        if (user == null) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("/".equals(path) && "GET".equalsIgnoreCase(method)) {
            showList(exchange, user);
        } else if ("/tasks/new".equals(path)) {
            handleNew(exchange, user);
        } else if ("/tasks/edit".equals(path)) {
            handleEdit(exchange, user);
        } else if ("/tasks/delete".equals(path) && "GET".equalsIgnoreCase(method)) {
            handleDelete(exchange, user);
        } else if ("/tasks/toggle".equals(path) && "GET".equalsIgnoreCase(method)) {
            handleToggle(exchange, user);
        } else {
            HttpUtil.sendHtml(exchange, 404, "<h1>404 Not Found</h1>");
        }
    }

    private void showList(HttpExchange exchange, User user) throws IOException {
        List<Task> tasks = taskDao.findByUserId(user.getId());
        List<Category> categories = categoryDao.findAll();
        HttpUtil.sendHtml(exchange, 200, HtmlTemplate.taskListPage(tasks, categories, user));
    }

    private void handleNew(HttpExchange exchange, User user) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            List<Category> categories = categoryDao.findAll();
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.taskFormPage(null, categories, user));
        } else {
            Map<String, String> form = HttpUtil.parseForm(exchange);
            String name = form.getOrDefault("name", "").trim();
            String dateStr = form.getOrDefault("date", "");
            String status = form.getOrDefault("status", "В работе");
            String catStr = form.getOrDefault("category_id", "");

            LocalDate date = dateStr.isBlank() ? LocalDate.now() : LocalDate.parse(dateStr);
            Integer categoryId = catStr.isBlank() ? null : Integer.parseInt(catStr);

            taskDao.create(name, status, date, false, user.getId(), categoryId);
            HttpUtil.redirect(exchange, "/");
        }
    }

    private void handleEdit(HttpExchange exchange, User user) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = HttpUtil.parseQuery(query);
        int id = Integer.parseInt(params.getOrDefault("id", "0"));

        Optional<Task> taskOpt = taskDao.findById(id);
        if (taskOpt.isEmpty() || (taskOpt.get().getUserId() != null && taskOpt.get().getUserId() != user.getId() && !user.isAdmin())) {
            HttpUtil.redirect(exchange, "/");
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            List<Category> categories = categoryDao.findAll();
            HttpUtil.sendHtml(exchange, 200, HtmlTemplate.taskFormPage(taskOpt.get(), categories, user));
        } else {
            Map<String, String> form = HttpUtil.parseForm(exchange);
            String name = form.getOrDefault("name", "").trim();
            String dateStr = form.getOrDefault("date", "");
            String status = form.getOrDefault("status", "В работе");
            String catStr = form.getOrDefault("category_id", "");

            LocalDate date = dateStr.isBlank() ? LocalDate.now() : LocalDate.parse(dateStr);
            Integer categoryId = catStr.isBlank() ? null : Integer.parseInt(catStr);
            boolean checked = "Выполнено".equals(status);

            taskDao.update(id, name, status, date, checked, categoryId);
            HttpUtil.redirect(exchange, "/");
        }
    }

    private void handleDelete(HttpExchange exchange, User user) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = HttpUtil.parseQuery(query);
        int id = Integer.parseInt(params.getOrDefault("id", "0"));

        Optional<Task> taskOpt = taskDao.findById(id);
        if (taskOpt.isPresent()) {
            Task t = taskOpt.get();
            if (t.getUserId() == null || t.getUserId() == user.getId() || user.isAdmin()) {
                taskDao.delete(id);
            }
        }
        HttpUtil.redirect(exchange, "/");
    }

    private void handleToggle(HttpExchange exchange, User user) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = HttpUtil.parseQuery(query);
        int id = Integer.parseInt(params.getOrDefault("id", "0"));
        taskDao.toggleChecked(id);
        HttpUtil.redirect(exchange, "/");
    }

    private User requireAuth(HttpExchange exchange) throws IOException {
        String token = SessionManager.extractToken(exchange);
        User user = SessionManager.getUser(token);
        if (user == null) {
            HttpUtil.redirect(exchange, "/login");
            return null;
        }
        return user;
    }
}
