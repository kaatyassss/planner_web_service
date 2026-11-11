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
import org.springframework.web.bind.annotation.ResponseBody;

// Обрабатывает запросы к страницам авторизации через Spring MVC
@Controller
public class AuthController {

    // Пока используем существующий DAO без изменения способа доступа к БД
    private final UserDao userDao = new UserDao();

    // Показывает существующую форму входа при GET запросе на /login
    @GetMapping(value = "/login", produces = "text/html;charset=UTF-8")
    // Возвращает HTML прямо в ответе, а не имя файла шаблона
    @ResponseBody
    public String loginPage() {
        return HtmlTemplate.loginPage(null);
    }

    // Принимает поля существующей HTML формы и проверяет учётные данные
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam(name = "username", defaultValue = "") String username,
            @RequestParam(name = "password", defaultValue = "") String password,
            HttpServletRequest request) {
        User user = userDao.findByUsername(username.trim()).orElse(null);
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                    .body(HtmlTemplate.loginPage("Неверный логин или пароль"));
        }
        // Берется сессия и меняется ее id (для того чтобы не использовать старую сессию), в сессию добавляем пользователя
        HttpSession session = request.getSession();
        request.changeSessionId();
        session.setAttribute("user", user);

        // После успешного входа направляем браузер на главную страницу
        return ResponseEntity.status(302).header("Location", "/").build();
    }
    // Завершает текущую сессию при переходе по существующей ссылке «Выйти»
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // Не создаём новую сессию, если пользователь уже вышел или ещё не входил
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Удаляет данные сессии, включая сохранённого пользователя
            session.invalidate();
        }
        return ResponseEntity.status(302).header("Location", "/login").build();
    }
}
