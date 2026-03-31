package com.kaatyassss.planner.web;

import com.kaatyassss.planner.web.db.DbInit;
import com.kaatyassss.planner.web.handler.AdminHandler;
import com.kaatyassss.planner.web.handler.CategoryHandler;
import com.kaatyassss.planner.web.handler.LoginHandler;
import com.kaatyassss.planner.web.handler.LogoutHandler;
import com.kaatyassss.planner.web.handler.RegisterHandler;
import com.kaatyassss.planner.web.handler.TaskHandler;
import com.kaatyassss.planner.web.util.EnvLoader;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebServer {

    public static void main(String[] args) throws IOException {
        DbInit.createTables();

        int port = Integer.parseInt(EnvLoader.get("SERVER_PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        TaskHandler taskHandler = new TaskHandler();
        CategoryHandler categoryHandler = new CategoryHandler();
        AdminHandler adminHandler = new AdminHandler();

        // Авторизация
        server.createContext("/login",    new LoginHandler());
        server.createContext("/logout",   new LogoutHandler());
        server.createContext("/register", new RegisterHandler());

        // Задачи
        server.createContext("/",             taskHandler);
        server.createContext("/tasks/new",    taskHandler);
        server.createContext("/tasks/edit",   taskHandler);
        server.createContext("/tasks/delete", taskHandler);
        server.createContext("/tasks/toggle", taskHandler);

        // Категории
        server.createContext("/categories",        categoryHandler);
        server.createContext("/categories/new",    categoryHandler);
        server.createContext("/categories/edit",   categoryHandler);
        server.createContext("/categories/delete", categoryHandler);

        // Администрирование
        server.createContext("/admin",        adminHandler);
        server.createContext("/admin/new",    adminHandler);
        server.createContext("/admin/edit",   adminHandler);
        server.createContext("/admin/delete", adminHandler);

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("Веб-сервер запущен на http://localhost:" + port);
        System.out.println("Войдите как admin / admin123");
    }
}
