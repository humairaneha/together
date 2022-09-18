package com.apptask.together.Models;

import java.util.Date;

public class Ideas {

    String username,userId,profilepic,title,idea,useremail,time,date,postId;
    long counter;

    public Ideas(String username, String userId, String profilepic, String title, String idea, String useremail, String time, String date, String postId, long counter) {
        this.username = username;
        this.userId = userId;
        this.profilepic = profilepic;
        this.title = title;
        this.idea = idea;
        this.useremail = useremail;
        this.time = time;
        this.date = date;
        this.postId = postId;
        this.counter = counter;
    }

    public Ideas() {
    }

    public Ideas(String username, String userId, String profilepic, String title, String idea, String useremail, String time, String date, String postId) {
        this.username = username;
        this.userId = userId;
        this.profilepic = profilepic;
        this.title = title;
        this.idea = idea;
        this.useremail = useremail;
        this.time = time;
        this.date = date;
        this.postId = postId;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdea() {
        return idea;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }
}
