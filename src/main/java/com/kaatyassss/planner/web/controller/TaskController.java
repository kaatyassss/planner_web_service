package com.kaatyassss.planner.web.controller;

import com.kaatyassss.planner.web.dao.CategoryDao;
import com.kaatyassss.planner.web.dao.TaskDao;
import com.kaatyassss.planner.web.model.User;
import com.kaatyassss.planner.web.util.HtmlTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

// Обрабатывает страницы задач через Spring MVC
@Controller
public class TaskController {

    // Пока сохраняем существующие DAO и SQL запросы
    private final TaskDao taskDao = new TaskDao();
    private final CategoryDao categoryDao = new CategoryDao();

    // Показывает задачи текущего пользователя на главной странице
    @GetMapping("/")
    public ResponseEntity<String> taskList(HttpServletRequest request) {
        // false запрещает создавать новую сессию для посетителя, который ещё не вошёл
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            // Без авторизации направляем браузер на форму входа
            return ResponseEntity.status(302).header("Location", "/login").build();
        }

        // Загружаем только задачи владельца сессии и используем существующий HTML
        String html = HtmlTemplate.taskListPage(
                taskDao.findByUserId(user.getId()), categoryDao.findAll(), user);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(html);
    }
    // GET показывает существующую форму создания задачи со списком категорий
    @GetMapping("/tasks/new")
    public ResponseEntity<String> newTaskForm(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(302).header("Location", "/login").build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(HtmlTemplate.taskFormPage(null, categoryDao.findAll(), user));
    }

    // POST принимает поля формы и сохраняет задачу от имени вошедшего пользователя
    @PostMapping("/tasks/new")
    public ResponseEntity<String> createTask(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "date", defaultValue = "") String date,
            @RequestParam(name = "status", defaultValue = "В работе") String status,
            @RequestParam(name = "category_id", defaultValue = "") String categoryId,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(302).header("Location", "/login").build();
        }

        // Сохраняем прежние правила: пустая дата означает сегодня, категория необязательна
        LocalDate taskDate = date.isBlank() ? LocalDate.now() : LocalDate.parse(date);
        Integer selectedCategoryId = categoryId.isBlank() ? null : Integer.parseInt(categoryId);
        taskDao.create(name.trim(), status, taskDate, false, user.getId(), selectedCategoryId);

        // Перенаправление предотвращает повторное создание при обновлении страницы
        return ResponseEntity.status(302).header("Location", "/").build();
    }
}
