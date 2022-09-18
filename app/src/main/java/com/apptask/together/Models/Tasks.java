package com.apptask.together.Models;

import android.renderscript.RenderScript;

import java.util.Date;

public class Tasks {
     String task,taskId,userId,userName,userEmail;
    Date due_date,created_at;
    Boolean is_done;

    public Tasks() {
    }


    public Tasks(String task, String taskId, String userId, String userName, String userEmail, Date due_date, Date created_at, Boolean is_done) {
        this.task = task;
        this.taskId = taskId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.due_date = due_date;
        this.created_at = created_at;
        this.is_done = is_done;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Boolean getIs_done() {
        return is_done;
    }

    public void setIs_done(Boolean is_done) {
        this.is_done = is_done;
    }
}
