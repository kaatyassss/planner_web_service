package com.kaatyassss.planner.web.util;

import com.kaatyassss.planner.web.model.Category;
import com.kaatyassss.planner.web.model.Task;
import com.kaatyassss.planner.web.model.User;

import java.util.List;

public final class HtmlTemplate {

    private HtmlTemplate() {}

    private static String layout(String title, String body, User user) {
        String nav = user == null ? "" : """
                <nav>
                  <a href="/">Задачи</a>
                  <a href="/categories">Категории</a>
                  %s
                  <a href="/logout">Выйти (%s)</a>
                </nav>
                """.formatted(
                user.isAdmin() ? "<a href=\"/admin\">Админка</a>" : "",
                HttpUtil.escape(user.getUsername())
        );
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s — Планировщик</title>
                  <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body { font-family: Arial, sans-serif; background: #f5f5f5; color: #333; }
                    nav { background: #4a90d9; padding: 12px 24px; display: flex; gap: 16px; align-items: center; }
                    nav a { color: #fff; text-decoration: none; font-weight: bold; }
                    nav a:hover { text-decoration: underline; }
                    .container { max-width: 900px; margin: 32px auto; padding: 0 16px; }
                    h1 { margin-bottom: 20px; }
                    h2 { margin-bottom: 16px; }
                    table { width: 100%%; border-collapse: collapse; background: #fff; border-radius: 6px; overflow: hidden; }
                    th, td { padding: 10px 14px; border-bottom: 1px solid #e0e0e0; text-align: left; }
                    th { background: #4a90d9; color: #fff; }
                    tr:last-child td { border-bottom: none; }
                    .btn { display: inline-block; padding: 7px 16px; border-radius: 4px; text-decoration: none;
                           border: none; cursor: pointer; font-size: 14px; }
                    .btn-primary { background: #4a90d9; color: #fff; }
                    .btn-danger  { background: #e74c3c; color: #fff; }
                    .btn-sm      { padding: 4px 10px; font-size: 13px; }
                    form .field  { margin-bottom: 14px; }
                    form label   { display: block; margin-bottom: 4px; font-weight: bold; }
                    form input, form select, form textarea {
                      width: 100%%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; }
                    .card { background: #fff; padding: 24px; border-radius: 8px;
                            box-shadow: 0 2px 8px rgba(0,0,0,.1); max-width: 420px; margin: 60px auto; }
                    .error { color: #e74c3c; margin-bottom: 12px; }
                    .checked-row td { text-decoration: line-through; color: #999; }
                    .actions { display: flex; gap: 8px; margin-bottom: 16px; }
                  </style>
                </head>
                <body>
                  %s
                  <div class="container">
                    %s
                  </div>
                </body>
                </html>
                """.formatted(title, nav, body);
    }

    public static String loginPage(String error) {
        String err = error != null ? "<p class=\"error\">" + HttpUtil.escape(error) + "</p>" : "";
        String body = """
                <div class="card">
                  <h2>Вход в систему</h2>
                  <br>
                  %s
                  <form method="post" action="/login">
                    <div class="field">
                      <label>Логин</label>
                      <input type="text" name="username" required autofocus>
                    </div>
                    <div class="field">
                      <label>Пароль</label>
                      <input type="password" name="password" required>
                    </div>
                    <button class="btn btn-primary" type="submit">Войти</button>
                  </form>
                  <p style="margin-top:16px;font-size:14px">Нет аккаунта? <a href="/register">Зарегистрироваться</a></p>
                </div>
                """.formatted(err);
        return layout("Вход", body, null);
    }

    public static String registerPage(String error) {
        String err = error != null ? "<p class=\"error\">" + HttpUtil.escape(error) + "</p>" : "";
        String body = """
                <div class="card">
                  <h2>Регистрация</h2>
                  <br>
                  %s
                  <form method="post" action="/register">
                    <div class="field">
                      <label>Логин</label>
                      <input type="text" name="username" required autofocus>
                    </div>
                    <div class="field">
                      <label>Email</label>
                      <input type="email" name="email" required>
                    </div>
                    <div class="field">
                      <label>Пароль</label>
                      <input type="password" name="password" required>
                    </div>
                    <div class="field">
                      <label>Повторите пароль</label>
                      <input type="password" name="confirm" required>
                    </div>
                    <button class="btn btn-primary" type="submit">Зарегистрироваться</button>
                  </form>
                  <p style="margin-top:16px;font-size:14px">Уже есть аккаунт? <a href="/login">Войти</a></p>
                </div>
                """.formatted(err);
        return layout("Регистрация", body, null);
    }

    public static String taskListPage(List<Task> tasks, List<Category> categories, User user) {
        StringBuilder rows = new StringBuilder();
        for (Task t : tasks) {
            String cls = t.isChecked() ? " class=\"checked-row\"" : "";
            rows.append("""
                    <tr%s>
                      <td>%s</td>
                      <td>%s</td>
                      <td>%s</td>
                      <td>%s</td>
                      <td>
                        <a class="btn btn-sm btn-primary" href="/tasks/edit?id=%d">Изменить</a>
                        <a class="btn btn-sm" href="/tasks/toggle?id=%d" style="background:#27ae60;color:#fff">
                          %s
                        </a>
                        <a class="btn btn-sm btn-danger" href="/tasks/delete?id=%d"
                           onclick="return confirm('Удалить задачу?')">Удалить</a>
                      </td>
                    </tr>
                    """.formatted(
                    cls,
                    HttpUtil.escape(t.getName()),
                    HttpUtil.escape(t.getStatus()),
                    t.getTaskDate(),
                    t.getCategoryName() != null ? HttpUtil.escape(t.getCategoryName()) : "—",
                    t.getId(), t.getId(),
                    t.isChecked() ? "Возобновить" : "Выполнено",
                    t.getId()
            ));
        }

        StringBuilder catOptions = new StringBuilder("<option value=\"\">— Без категории —</option>");
        for (Category c : categories) {
            catOptions.append("<option value=\"%d\">%s</option>".formatted(c.getId(), HttpUtil.escape(c.getName())));
        }

        String body = """
                <h1>Мои задачи</h1>
                <div class="actions">
                  <a class="btn btn-primary" href="/tasks/new">+ Новая задача</a>
                </div>
                <table>
                  <thead><tr><th>Название</th><th>Статус</th><th>Дата</th><th>Категория</th><th>Действия</th></tr></thead>
                  <tbody>%s</tbody>
                </table>
                """.formatted(rows);
        return layout("Задачи", body, user);
    }

    public static String taskFormPage(Task task, List<Category> categories, User user) {
        boolean isEdit = task != null && task.getId() > 0;
        String action = isEdit ? "/tasks/edit?id=" + task.getId() : "/tasks/new";
        String name = task != null ? HttpUtil.escape(task.getName()) : "";
        String date = task != null && task.getTaskDate() != null ? task.getTaskDate().toString() : "";
        String status = task != null ? task.getStatus() : "В работе";
        int catId = task != null && task.getCategoryId() != null ? task.getCategoryId() : 0;

        StringBuilder catOptions = new StringBuilder("<option value=\"\">— Без категории —</option>");
        for (Category c : categories) {
            String sel = c.getId() == catId ? " selected" : "";
            catOptions.append("<option value=\"%d\"%s>%s</option>".formatted(c.getId(), sel, HttpUtil.escape(c.getName())));
        }

        String[] statuses = {"В работе", "Выполнено", "Отложено"};
        StringBuilder statusOptions = new StringBuilder();
        for (String s : statuses) {
            String sel = s.equals(status) ? " selected" : "";
            statusOptions.append("<option%s>%s</option>".formatted(sel, s));
        }

        String body = """
                <h1>%s задачу</h1>
                <form method="post" action="%s" style="max-width:480px">
                  <div class="field">
                    <label>Название</label>
                    <input type="text" name="name" value="%s" required>
                  </div>
                  <div class="field">
                    <label>Дата</label>
                    <input type="date" name="date" value="%s" required>
                  </div>
                  <div class="field">
                    <label>Статус</label>
                    <select name="status">%s</select>
                  </div>
                  <div class="field">
                    <label>Категория</label>
                    <select name="category_id">%s</select>
                  </div>
                  <button class="btn btn-primary" type="submit">%s</button>
                  <a class="btn" href="/" style="background:#ccc">Отмена</a>
                </form>
                """.formatted(
                isEdit ? "Изменить" : "Добавить",
                action, name, date, statusOptions, catOptions,
                isEdit ? "Сохранить" : "Создать"
        );
        return layout(isEdit ? "Редактировать задачу" : "Новая задача", body, user);
    }

    public static String categoryListPage(List<Category> categories, User user) {
        StringBuilder rows = new StringBuilder();
        for (Category c : categories) {
            rows.append("""
                    <tr>
                      <td>%s</td>
                      <td>%s</td>
                      <td>
                        <a class="btn btn-sm btn-primary" href="/categories/edit?id=%d">Изменить</a>
                        <a class="btn btn-sm btn-danger" href="/categories/delete?id=%d"
                           onclick="return confirm('Удалить категорию?')">Удалить</a>
                      </td>
                    </tr>
                    """.formatted(
                    HttpUtil.escape(c.getName()),
                    c.getDescription() != null ? HttpUtil.escape(c.getDescription()) : "—",
                    c.getId(), c.getId()
            ));
        }
        String body = """
                <h1>Категории</h1>
                <div class="actions">
                  <a class="btn btn-primary" href="/categories/new">+ Новая категория</a>
                </div>
                <table>
                  <thead><tr><th>Название</th><th>Описание</th><th>Действия</th></tr></thead>
                  <tbody>%s</tbody>
                </table>
                """.formatted(rows);
        return layout("Категории", body, user);
    }

    public static String categoryFormPage(Category cat, User user) {
        boolean isEdit = cat != null && cat.getId() > 0;
        String action = isEdit ? "/categories/edit?id=" + cat.getId() : "/categories/new";
        String name = cat != null ? HttpUtil.escape(cat.getName()) : "";
        String desc = cat != null && cat.getDescription() != null ? HttpUtil.escape(cat.getDescription()) : "";
        String body = """
                <h1>%s категорию</h1>
                <form method="post" action="%s" style="max-width:480px">
                  <div class="field">
                    <label>Название</label>
                    <input type="text" name="name" value="%s" required>
                  </div>
                  <div class="field">
                    <label>Описание</label>
                    <textarea name="description" rows="3">%s</textarea>
                  </div>
                  <button class="btn btn-primary" type="submit">%s</button>
                  <a class="btn" href="/categories" style="background:#ccc">Отмена</a>
                </form>
                """.formatted(
                isEdit ? "Изменить" : "Добавить",
                action, name, desc,
                isEdit ? "Сохранить" : "Создать"
        );
        return layout(isEdit ? "Редактировать категорию" : "Новая категория", body, user);
    }

    public static String adminPage(List<User> users, User currentUser) {
        StringBuilder rows = new StringBuilder();
        for (User u : users) {
            String del = u.getId() == currentUser.getId() ? "" :
                    "<a class=\"btn btn-sm btn-danger\" href=\"/admin/delete?id=%d\" onclick=\"return confirm('Удалить пользователя?')\">Удалить</a>".formatted(u.getId());
            rows.append("""
                    <tr>
                      <td>%d</td>
                      <td>%s</td>
                      <td>%s</td>
                      <td>%s</td>
                      <td>%s</td>
                      <td>
                        <a class="btn btn-sm btn-primary" href="/admin/edit?id=%d">Изменить</a>
                        %s
                      </td>
                    </tr>
                    """.formatted(
                    u.getId(),
                    HttpUtil.escape(u.getUsername()),
                    HttpUtil.escape(u.getEmail()),
                    HttpUtil.escape(u.getRole()),
                    u.getCreatedAt().toLocalDate(),
                    u.getId(), del
            ));
        }
        String body = """
                <h1>Управление пользователями</h1>
                <div class="actions">
                  <a class="btn btn-primary" href="/admin/new">+ Новый пользователь</a>
                </div>
                <table>
                  <thead><tr><th>ID</th><th>Логин</th><th>Email</th><th>Роль</th><th>Создан</th><th>Действия</th></tr></thead>
                  <tbody>%s</tbody>
                </table>
                """.formatted(rows);
        return layout("Администрирование", body, currentUser);
    }

    public static String userFormPage(User editUser, User currentUser) {
        boolean isEdit = editUser != null && editUser.getId() > 0;
        String action = isEdit ? "/admin/edit?id=" + editUser.getId() : "/admin/new";
        String username = editUser != null ? HttpUtil.escape(editUser.getUsername()) : "";
        String email = editUser != null ? HttpUtil.escape(editUser.getEmail()) : "";
        String role = editUser != null ? editUser.getRole() : "user";

        String roleOptions = """
                <option value="user"%s>Пользователь</option>
                <option value="admin"%s>Администратор</option>
                """.formatted(
                "user".equals(role) ? " selected" : "",
                "admin".equals(role) ? " selected" : ""
        );

        String passwordField = isEdit ? """
                <div class="field">
                  <label>Новый пароль (оставьте пустым, чтобы не менять)</label>
                  <input type="password" name="password">
                </div>
                """ : """
                <div class="field">
                  <label>Пароль</label>
                  <input type="password" name="password" required>
                </div>
                """;

        String body = """
                <h1>%s пользователя</h1>
                <form method="post" action="%s" style="max-width:480px">
                  <div class="field">
                    <label>Логин</label>
                    <input type="text" name="username" value="%s" required>
                  </div>
                  <div class="field">
                    <label>Email</label>
                    <input type="email" name="email" value="%s" required>
                  </div>
                  %s
                  <div class="field">
                    <label>Роль</label>
                    <select name="role">%s</select>
                  </div>
                  <button class="btn btn-primary" type="submit">%s</button>
                  <a class="btn" href="/admin" style="background:#ccc">Отмена</a>
                </form>
                """.formatted(
                isEdit ? "Изменить" : "Создать",
                action, username, email, passwordField, roleOptions,
                isEdit ? "Сохранить" : "Создать"
        );
        return layout(isEdit ? "Редактировать пользователя" : "Новый пользователь", body, currentUser);
    }
}
