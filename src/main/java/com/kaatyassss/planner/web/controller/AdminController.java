package com.kaatyassss.planner.web.controller;

import com.kaatyassss.planner.web.dao.UserDao;
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

// Управление пользователями через Spring MVC с сохранением существующих форм
@Controller
public class AdminController {

    private final UserDao userDao = new UserDao();

    // Показывает список пользователей только администратору
    @GetMapping("/admin")
    public ResponseEntity<String> users(HttpServletRequest request) {
        User user = currentUser(request);
        ResponseEntity<String> denied = checkAccess(user);
        if (denied != null) return denied;
        return html(HtmlTemplate.adminPage(userDao.findAll(), user));
    }

    // Показывает пустую форму создания пользователя
    @GetMapping("/admin/new")
    public ResponseEntity<String> newUserForm(HttpServletRequest request) {
        User user = currentUser(request);
        ResponseEntity<String> denied = checkAccess(user);
        if (denied != null) return denied;
        return html(HtmlTemplate.userFormPage(null, user));
    }

    // Принимает поля формы и сохраняет прежнюю проверку логина и пароля
    @PostMapping("/admin/new")
    public ResponseEntity<String> createUser(
            @RequestParam(name = "username", defaultValue = "") String username,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "password", defaultValue = "") String password,
            @RequestParam(name = "role", defaultValue = "user") String role,
            HttpServletRequest request) {
        ResponseEntity<String> denied = checkAccess(currentUser(request));
        if (denied != null) return denied;
        if (!username.isBlank() && !password.isBlank()) {
            userDao.create(username.trim(), password.trim(), email.trim(), role);
        }
        return redirect("/admin");
    }

    // Загружает пользователя для заполнения формы редактирования
    @GetMapping("/admin/edit")
    public ResponseEntity<String> editUserForm(
            @RequestParam(name = "id") int id, HttpServletRequest request) {
        User user = currentUser(request);
        ResponseEntity<String> denied = checkAccess(user);
        if (denied != null) return denied;
        User editedUser = userDao.findById(id).orElse(null);
        if (editedUser == null) return redirect("/admin");
        return html(HtmlTemplate.userFormPage(editedUser, user));
    }

    // Повторно проверяет роль при сохранении: POST можно отправить без открытия формы
    @PostMapping("/admin/edit")
    public ResponseEntity<String> updateUser(
            @RequestParam(name = "id") int id,
            @RequestParam(name = "username", defaultValue = "") String username,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "role", defaultValue = "user") String role,
            @RequestParam(name = "password", defaultValue = "") String password,
            HttpServletRequest request) {
        ResponseEntity<String> denied = checkAccess(currentUser(request));
        if (denied != null) return denied;
        if (userDao.findById(id).isPresent() && !username.isBlank()) {
            userDao.update(id, username.trim(), email.trim(), role);
            // Пустое поле пароля оставляет текущий пароль без изменений
            if (!password.isBlank()) userDao.updatePassword(id, password.trim());
        }
        return redirect("/admin");
    }

    // Сохраняет существующую ссылку удаления и запрет удаления самого себя
    @GetMapping("/admin/delete")
    public ResponseEntity<String> deleteUser(
            @RequestParam(name = "id") int id, HttpServletRequest request) {
        User user = currentUser(request);
        ResponseEntity<String> denied = checkAccess(user);
        if (denied != null) return denied;
        if (id != user.getId()) userDao.delete(id);
        return redirect("/admin");
    }

    // Получает пользователя из существующей сессии, не создавая сессию для гостя
    private User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    // Гостя отправляем на вход, обычного пользователя - к задачам
    // null означает, что администратору разрешено продолжить обработку
    private ResponseEntity<String> checkAccess(User user) {
        if (user == null) return redirect("/login");
        if (!user.isAdmin()) return redirect("/");
        return null;
    }

    // Формирует HTML-ответ с кодировкой существующих страниц
    private ResponseEntity<String> html(String body) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(body);
    }

    private ResponseEntity<String> redirect(String location) {
        return ResponseEntity.status(302).header("Location", location).build();
    }
}
