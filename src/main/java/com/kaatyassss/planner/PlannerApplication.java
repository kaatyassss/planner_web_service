package com.kaatyassss.planner;

import com.kaatyassss.planner.web.util.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

// Включаем автоконфигурацию Spring Boot
@SpringBootApplication
public class PlannerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PlannerApplication.class);
        // Читаем порт прежним способом: из .env, затем из окружения; по умолчанию 8080
        // Передаём его как настройку Spring до создания встроенного веб-сервера
        application.setDefaultProperties(Map.of("server.port", EnvLoader.get("SERVER_PORT", "8080")));
        // Создаёт контекст Spring и запускает встроенный веб-сервер
        application.run(args);
    }
}
