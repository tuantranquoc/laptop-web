package com.example.demo.note;


import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class StaticVariables {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String DENIED = "Access denied!";
    public static final String SUCCESS = "Request successfully!";
    public static final String FAILURE = "Request failure contact ADMIN for more information!";
    public static final String USER_EXIST = "User name already exist!";
    public static final String USER_NOT_EXIST = "User not exist!";
    public static final String LOGIN_FAILURE = "Wrong user name or password!";
    public static final String LOGIN_SUCCESS = "Login successfully!";
    public static final String LOGOUT_SUCCESS = "Logout successfully!";
    public static final String ALREADY_LOGIN = "Already login!";
    public static String CURRENT_USER;
    public static String HOST = "http://localhost:8090/app/api/";
    public static String FIND_CURRENT = "findCurrent";
    public static String FIND_ALL = "findAll";
    public static String FIND_ONE = "findOne/";
    public static String SIGN = "sign";
    public static String DELETE = "delete";
    public static String SWAP_ROLE = "swap";
    public static String POST = "POST";
    public static String PUT = "PUT";
    public static String GET = "GET";
    public static String HEAD = "HEAD";
    public static String SELF = "SELF";
    public static final String KEY = "123456789";
    public static String CODE;
    public final static String TOKEN_EXPIRED = "Token expired";
    public static final String AUTHORIZATION_CODE = "This is the right code!";
    public static final String PRODUCT_NOT_EXIST = "Can't find this product!";
    public static final String PRODUCT_EXIST = "Product name already exist!";
    public static final String PROCESS = "process";
    public static final String PAID = "paid";
    public static final String P_CODE = "ok";

    public static String AUTH_CODE = "OxnE82yW8WdhfYKE\n" +
            "IHJvjyDrjlBb2Vfm\n" +
            "cMKZQLNh9E5ryoGg\n" +
            "cxO75TescFB8NQjU\n" +
            "VxLVrpZSaEnjJjqa\n" +
            "S92YfSb8op3cayTw\n" +
            "neO1Dj8hLWLAxqmF\n" +
            "ZPI4G8pbL4oxQNPV\n" +
            "bNRgSltdTxacOIpA\n" +
            "GVyoXV3mkWQ7eirW";
   public static RSAPublicKey PUBLIC_KEY;
   public static RSAPrivateKey PRIVATE_KEY;
}
