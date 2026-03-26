package com.kaatyassss.planner.web.dao;

import com.kaatyassss.planner.web.db.DbConnection;
import com.kaatyassss.planner.web.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDao {

    public List<Task> findByUserId(int userId) {
        String sql = """
                SELECT t.*, c.name as category_name
                FROM tasks t
                LEFT JOIN categories c ON t.category_id = c.id
                WHERE t.user_id = ?
                ORDER BY t.task_date
                """;
        List<Task> list = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Task> findAll() {
        String sql = """
                SELECT t.*, c.name as category_name
                FROM tasks t
                LEFT JOIN categories c ON t.category_id = c.id
                ORDER BY t.task_date
                """;
        List<Task> list = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Optional<Task> findById(int id) {
        String sql = """
                SELECT t.*, c.name as category_name
                FROM tasks t
                LEFT JOIN categories c ON t.category_id = c.id
                WHERE t.id = ?
                """;
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Task create(String name, String status, java.time.LocalDate date, boolean checked, Integer userId, Integer categoryId) {
        String sql = "INSERT INTO tasks (name, status, task_date, checked, user_id, category_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, status);
            ps.setDate(3, Date.valueOf(date));
            ps.setBoolean(4, checked);
            if (userId != null) ps.setInt(5, userId); else ps.setNull(5, Types.INTEGER);
            if (categoryId != null) ps.setInt(6, categoryId); else ps.setNull(6, Types.INTEGER);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return findById(id).orElseThrow();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Не удалось создать задачу");
    }

    public void update(int id, String name, String status, java.time.LocalDate date, boolean checked, Integer categoryId) {
        String sql = "UPDATE tasks SET name = ?, status = ?, task_date = ?, checked = ?, category_id = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, status);
            ps.setDate(3, Date.valueOf(date));
            ps.setBoolean(4, checked);
            if (categoryId != null) ps.setInt(5, categoryId); else ps.setNull(5, Types.INTEGER);
            ps.setInt(6, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void toggleChecked(int id) {
        String sql = """
                UPDATE tasks
                SET checked = NOT checked,
                    status  = CASE WHEN checked THEN 'В работе' ELSE 'Выполнено' END
                WHERE id = ?
                """;
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Task map(ResultSet rs) throws SQLException {
        Task t = new Task(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("status"),
                rs.getDate("task_date").toLocalDate(),
                rs.getBoolean("checked"),
                (Integer) rs.getObject("user_id"),
                (Integer) rs.getObject("category_id")
        );
        t.setCategoryName(rs.getString("category_name"));
        return t;
    }
}
