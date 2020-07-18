package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartHistory {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ShoppingCartInfo> getList() {
        return list;
    }

    public void setList(List<ShoppingCartInfo> list) {
        this.list = list;
    }

    List<ShoppingCartInfo> list = new ArrayList<>();
}
