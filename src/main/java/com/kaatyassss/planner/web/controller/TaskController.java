package com.kaatyassss.planner.web.controller;

import com.kaatyassss.planner.web.dao.CategoryDao;
import com.kaatyassss.planner.web.dao.TaskDao;
import com.kaatyassss.planner.web.model.Task;
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
    // GET загружает задачу по id и показывает форму с текущими значениями
    @GetMapping("/tasks/edit")
    public ResponseEntity<String> editTaskForm(
            @RequestParam(name = "id") int id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(302).header("Location", "/login").build();
        }

        Task task = taskDao.findById(id).orElse(null);
        // Редактирование доступно владельцу задачи или администратору
        if (task == null || (!user.isAdmin()
                && !Integer.valueOf(user.getId()).equals(task.getUserId()))) {
            return ResponseEntity.status(302).header("Location", "/").build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(HtmlTemplate.taskFormPage(task, categoryDao.findAll(), user));
    }

    // POST сохраняет поля формы редактирования и возвращает пользователя к списку
    @PostMapping("/tasks/edit")
    public ResponseEntity<String> updateTask(
            @RequestParam(name = "id") int id,
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

        Task task = taskDao.findById(id).orElse(null);
        // Повторяем проверку при сохранении: POST можно отправить без открытия формы
        if (task == null || (!user.isAdmin()
                && !Integer.valueOf(user.getId()).equals(task.getUserId()))) {
            return ResponseEntity.status(302).header("Location", "/").build();
        }

        // Как в прежнем обработчике, отметка выполнения определяется выбранным статусом
        LocalDate taskDate = date.isBlank() ? LocalDate.now() : LocalDate.parse(date);
        Integer selectedCategoryId = categoryId.isBlank() ? null : Integer.parseInt(categoryId);
        boolean checked = "Выполнено".equals(status);
        taskDao.update(id, name.trim(), status, taskDate, checked, selectedCategoryId);
        return ResponseEntity.status(302).header("Location", "/").build();
    }
    // Сохраняем GET-маршрут, который используется существующей ссылкой удаления
    @GetMapping("/tasks/delete")
    public ResponseEntity<String> deleteTask(
            @RequestParam(name = "id") int id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(302).header("Location", "/login").build();
        }

        Task task = taskDao.findById(id).orElse(null);
        // Удалять задачу может её владелец или администратор
        if (task != null && (user.isAdmin()
                || Integer.valueOf(user.getId()).equals(task.getUserId()))) {
            taskDao.delete(id);
        }
        return ResponseEntity.status(302).header("Location", "/").build();
    }

    // Переключает выполнение по существующей ссылке.
    @GetMapping("/tasks/toggle")
    public ResponseEntity<String> toggleTask(
            @RequestParam(name = "id") int id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(302).header("Location", "/login").build();
        }

        Task task = taskDao.findById(id).orElse(null);
        // Проверяем владельца и здесь, чтобы нельзя было переключить чужую задачу по id
        if (task != null && (user.isAdmin()
                || Integer.valueOf(user.getId()).equals(task.getUserId()))) {
            // DAO одновременно изменяет отметку выполнения и текстовый статус
            taskDao.toggleChecked(id);
        }
        return ResponseEntity.status(302).header("Location", "/").build();
    }
}
