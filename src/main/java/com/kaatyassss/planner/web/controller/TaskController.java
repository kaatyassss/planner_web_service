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
}
