package com.kaatyassss.planner.web.model;

import java.time.LocalDate;

public class Task {

    private int id;
    private String name;
    private String status;
    private LocalDate taskDate;
    private boolean checked;
    private Integer userId;
    private Integer categoryId;
    private String categoryName;

    public Task() {}

    public Task(int id, String name, String status, LocalDate taskDate, boolean checked, Integer userId, Integer categoryId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.taskDate = taskDate;
        this.checked = checked;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getTaskDate() { return taskDate; }
    public void setTaskDate(LocalDate taskDate) { this.taskDate = taskDate; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
