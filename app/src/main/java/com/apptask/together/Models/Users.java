package com.apptask.together.Models;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Users {
    String username,mail,password,userId;
    String profilepic;

    public Users(String username, String mail, String password, String userId) {
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
    }

    public Users(String username, String mail, String password) {
        this.username = username;
        this.mail = mail;
        this.password = password;

    }

    public Users(String username, String mail, String password, String userId, String profilepic) {
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.profilepic = profilepic;
    }

    public Users() {
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return mail;
    }

    public void setEmail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



}
