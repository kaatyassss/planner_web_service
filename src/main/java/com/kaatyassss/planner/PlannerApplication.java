package com.kaatyassss.planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Включаем автоконфигурацию Spring Boot
@SpringBootApplication
public class PlannerApplication {

    public static void main(String[] args) {
        // Создаёт контекст Spring и запускает встроенный веб-сервер
        SpringApplication.run(PlannerApplication.class, args);
    }
}
