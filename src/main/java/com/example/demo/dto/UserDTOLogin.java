package com.example.demo.dto;

public class UserDTOLogin {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void add(String userName, String password){
        this.password = password;
        this.userName = userName;
    }

}
