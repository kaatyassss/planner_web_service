package com.kaatyassss.planner.web.controller;

import com.kaatyassss.planner.web.util.HtmlTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// Обрабатывает запросы к страницам авторизации через Spring MVC.
@Controller
public class AuthController {

    // Показывает существующую форму входа при GET-запросе на /login.
    @GetMapping(value = "/login", produces = "text/html;charset=UTF-8")
    // Возвращает HTML прямо в ответе, а не имя файла шаблона.
    @ResponseBody
    public String loginPage() {
        return HtmlTemplate.loginPage(null);
    }
}
