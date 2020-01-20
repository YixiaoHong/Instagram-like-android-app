package com.ECE1778A1.model;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private String userEmail,userName,userBio;

    public UserInfo(String userEmail, String userName, String userBio){
        this.userEmail = userEmail;
        this.userName = userName;
        this.userBio = userBio;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }
}
