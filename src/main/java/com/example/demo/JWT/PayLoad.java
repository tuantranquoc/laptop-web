package com.example.demo.JWT;

public class PayLoad {
   private String sub;
   private String name;
   private  String iat;

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }
}
