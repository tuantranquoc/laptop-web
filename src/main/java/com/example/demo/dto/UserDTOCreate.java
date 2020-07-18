package com.example.demo.dto;

public class UserDTOCreate {
    private String userName;
    private String password;
    private String role;
    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void add(String userName, String password,String mail){
        this.userName = userName;this.password =password;
        this.mail = mail;
        this.role= "USER";
    }
}
