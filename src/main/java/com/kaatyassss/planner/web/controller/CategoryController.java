package com.kaatyassss.planner.web.controller;

import com.kaatyassss.planner.web.dao.CategoryDao;
import com.kaatyassss.planner.web.model.Category;
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

// Переносит существующие страницы общих категорий на Spring MVC.
@Controller
public class CategoryController {

    private final CategoryDao categoryDao;

    // Единственный конструктор используется Spring для внедрения готовых DAO
    public CategoryController(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    // Список категорий доступен любому вошедшему пользователю, как и раньше.
    @GetMapping("/categories")
    public ResponseEntity<String> categoryList(HttpServletRequest request) {
        User user = currentUser(request);
        if (user == null) return redirect("/login");
        return html(HtmlTemplate.categoryListPage(categoryDao.findAll(), user));
    }

    // Показывает пустую форму создания категории.
    @GetMapping("/categories/new")
    public ResponseEntity<String> newCategoryForm(HttpServletRequest request) {
        User user = currentUser(request);
        if (user == null) return redirect("/login");
        return html(HtmlTemplate.categoryFormPage(null, user));
    }

    // Принимает поля формы; сохраняет прежнее правило непустого названия.
    @PostMapping("/categories/new")
    public ResponseEntity<String> createCategory(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "description", defaultValue = "") String description,
            HttpServletRequest request) {
        if (currentUser(request) == null) return redirect("/login");
        if (!name.isBlank()) categoryDao.create(name.trim(), description.trim());
        return redirect("/categories");
    }

    // Загружает категорию и подставляет её значения в существующую форму.
    @GetMapping("/categories/edit")
    public ResponseEntity<String> editCategoryForm(
            @RequestParam(name = "id") int id, HttpServletRequest request) {
        User user = currentUser(request);
        if (user == null) return redirect("/login");
        Category category = categoryDao.findById(id).orElse(null);
        if (category == null) return redirect("/categories");
        return html(HtmlTemplate.categoryFormPage(category, user));
    }

    // Проверяет наличие категории и сохраняет изменения через прежний DAO.
    @PostMapping("/categories/edit")
    public ResponseEntity<String> updateCategory(
            @RequestParam(name = "id") int id,
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "description", defaultValue = "") String description,
            HttpServletRequest request) {
        if (currentUser(request) == null) return redirect("/login");
        if (categoryDao.findById(id).isPresent() && !name.isBlank()) {
            categoryDao.update(id, name.trim(), description.trim());
        }
        return redirect("/categories");
    }

    // Сохраняет GET-маршрут удаления для совместимости с существующей ссылкой.
    @GetMapping("/categories/delete")
    public ResponseEntity<String> deleteCategory(
            @RequestParam(name = "id") int id, HttpServletRequest request) {
        if (currentUser(request) == null) return redirect("/login");
        categoryDao.delete(id);
        return redirect("/categories");
    }

    // Получает пользователя без создания новой сессии для гостя.
    private User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    // Общие методы формируют одинаковые HTML-ответы и перенаправления.
    private ResponseEntity<String> html(String body) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(body);
    }

    private ResponseEntity<String> redirect(String location) {
        return ResponseEntity.status(302).header("Location", location).build();
    }
}
