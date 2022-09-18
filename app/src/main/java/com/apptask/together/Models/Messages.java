package com.apptask.together.Models;

public class Messages {
    String username,text,userId;
    long timestamp;

    public Messages() {
    }

    public Messages(String username, String text, String userId, long timestamp) {
        this.username = username;
        this.text = text;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
