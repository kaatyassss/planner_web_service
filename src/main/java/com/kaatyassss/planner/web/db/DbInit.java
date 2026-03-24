package com.kaatyassss.planner.web.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbInit {

    private DbInit() {}

    public static void createTables() {
        String createUsers = """
                CREATE TABLE IF NOT EXISTS users (
                    id        SERIAL PRIMARY KEY,
                    username  VARCHAR(100) NOT NULL UNIQUE,
                    password  VARCHAR(255) NOT NULL,
                    email     VARCHAR(255) NOT NULL UNIQUE,
                    role      VARCHAR(20)  NOT NULL DEFAULT 'user',
                    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
                )
                """;

        String createCategories = """
                CREATE TABLE IF NOT EXISTS categories (
                    id          SERIAL PRIMARY KEY,
                    name        VARCHAR(100) NOT NULL UNIQUE,
                    description TEXT
                )
                """;

        String createTasks = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id          SERIAL PRIMARY KEY,
                    name        VARCHAR(255) NOT NULL,
                    status      VARCHAR(50)  NOT NULL DEFAULT 'В работе',
                    task_date   DATE         NOT NULL,
                    checked     BOOLEAN      NOT NULL DEFAULT FALSE,
                    user_id     INTEGER REFERENCES users(id) ON DELETE SET NULL,
                    category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL
                )
                """;

        String insertDefaultAdmin = """
                INSERT INTO users (username, password, email, role)
                VALUES ('admin', 'admin123', 'admin@planner.local', 'admin')
                ON CONFLICT (username) DO NOTHING
                """;

        String insertDefaultCategory = """
                INSERT INTO categories (name, description)
                VALUES ('Общее', 'Категория по умолчанию')
                ON CONFLICT (name) DO NOTHING
                """;

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsers);
            stmt.execute(createCategories);
            stmt.execute(createTasks);
            stmt.execute(insertDefaultAdmin);
            stmt.execute(insertDefaultCategory);

            System.out.println("Таблицы созданы успешно");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка инициализации базы данных", e);
        }
    }
}
