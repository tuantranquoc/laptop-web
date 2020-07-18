package com.example.demo.service;


import com.example.demo.config.RSAUtil;
import com.example.demo.dto.*;
import com.example.demo.model.Product;
import com.example.demo.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSession;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

import static com.example.demo.note.StaticVariables.*;

@Service
public final class UserControllerDTO {

    private final UserDetailsService userDetailsService;
    private final AuthenticatesService authenticatesService;
    private final ProductDetailsService productDetailsService;
    private final CustomerService customerService;

    @Autowired
    public UserControllerDTO(UserDetailsService userDetailsService, AuthenticatesService authenticatesService, ProductDetailsService productDetailsService, CustomerService customerService) {
        this.userDetailsService = userDetailsService;
        this.authenticatesService = authenticatesService;
        this.productDetailsService = productDetailsService;
        this.customerService = customerService;
    }

    // AUTHENTICATE

    public String login(HttpSession session, UserDTOLogin userDTOLogin) {
        if (CURRENT_USER != null && CURRENT_USER.equals(userDTOLogin.getUserName())) {
            return ALREADY_LOGIN;
        }
        if (!authenticatesService.login(userDTOLogin, session)) {
            return LOGIN_FAILURE;
        }
        return LOGIN_SUCCESS;
    }

    public String logout(HttpSession session) {
        Session.removeBySessionId(session.getId());
        session.invalidate();
        CURRENT_USER = null;
        return LOGOUT_SUCCESS;
    }

    public String signUp(UserDTOCreate userDTOCreate) {
        if (!userDetailsService.signUp(userDTOCreate)) {
            return USER_EXIST;
        }
        return SUCCESS;
    }

    public Object changeUserName(UserDTOLogin userDTOLogin) {
        userDetailsService.changeUserName(userDTOLogin.getUserName());
        return SUCCESS;
    }

    private int valid() {
        if (CURRENT_USER != null) {
            return 1;
        }
        return 0;
    }

    public Object swapRole(HttpSession session) {
        if (!authenticatesService.swapRole(session)) {
            return FAILURE;
        }
        return SUCCESS;
    }

    public Object addRole(HttpSession session) {
        if (!authenticatesService.addRole(session)) {
            return FAILURE;
        }
        return SUCCESS;
    }

    //USER RESOURCES
    private Object findOneUser(UserDTO userDTO) {
        if (userDTO == null) {
            return USER_NOT_EXIST;
        }
        return userDTO;
    }

    public Object findOneUser(int id, HttpSession session) {
        if (authenticatesService.hasRole(session).equals(ROLE_ADMIN)) {
            return findOneUser(userDetailsService.findOneUser(id));
        }
        return DENIED;
    }

    public Object findCurrent(HttpSession session) {
        if (authenticatesService.hasRole(session).equals(DENIED)) {
            return DENIED;
        }
        return findOneUser(userDetailsService.findCurrentUser(session));
    }

    public List<UserDTO> findAllUser(HttpSession session) {
        if (authenticatesService.hasRole(session).equals(ROLE_ADMIN)) {
            return userDetailsService.findAllUser();
        }
        return deniedObject();
    }

    private Object delete(boolean result) {
        if (!result) {
            return USER_NOT_EXIST;
        }
        return SUCCESS;
    }

    public Object delete(HttpSession session, int userId) {
        if (authenticatesService.hasRole(session).equals(ROLE_ADMIN)) {
            return delete(userDetailsService.delete(userId));
        }
        return DENIED;
    }

    private List<UserDTO> deniedObject() {
        List<UserDTO> userDTOS = new ArrayList<>();
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(DENIED);

        userDTO.setUserName(DENIED);
        userDTOS.add(userDTO);
        return userDTOS;
    }

    //THIS ONE FOR OAUTH 2.0


    public Object getAuthCode(int validId, int clientId) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException {
        try {
            if (valid() == 1 && validId == 1 && clientId == 1) {
                return Base64.getEncoder().encodeToString(RSAUtil.encrypt(AUTHORIZATION_CODE, RSAUtil.publicKey));//get auth code..
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;//get auth code..
    }

    //OAUTH 2.0

    @SuppressWarnings("ThrowablePrintedToSystemOut")
    private boolean validAuthCode(String authCode) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, NoSuchAlgorithmException {
        return AUTHORIZATION_CODE.equals(RSAUtil.decrypt(authCode, RSAUtil.privateKey));
    }

    public Object getUserResource(AccessToken accessToken, HttpSession session) {
        return userDetailsService.getUserResource(accessToken, Calendar.getInstance(),session);
    }
    //need to change clientId here.
    public Object response(String authCode, int clientId,HttpSession session) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException {
        try {
            if (validAuthCode(authCode)) {
                return userDetailsService.accessToken(clientId,session);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return DENIED;
    }


    //PRODUCT RESOURCES
    private Object findOneProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            return PRODUCT_NOT_EXIST;
        }
        return productDTO;
    }

    public List<Product> findAllProduct() {
        return productDetailsService.findAllProduct();
    }

    public Object findOneProduct(int productId) {
        return findOneProduct(productDetailsService.findOneProduct(productId));
    }

    public Object addProduct(HttpSession session, Product product) {

        if (authenticatesService.hasRole(session).equals(DENIED)) {
            return DENIED;
        }
        if (!productDetailsService.addProduct(product.getProductName(), product.getProductCost())) {
            return PRODUCT_EXIST;
        }
        return SUCCESS;
    }

    public Object changeProductName(HttpSession session, String productCurrentName, String productName) {
        if (authenticatesService.hasRole(session).equals(DENIED)) {
            return DENIED;
        }
        if (!productDetailsService.changeProductName(productCurrentName, productName)) {
            return PRODUCT_NOT_EXIST;
        }
        return SUCCESS;
    }

    public Object changeProductCost(HttpSession session, String productName, double productCost) {
        if (authenticatesService.hasRole(session).equals(DENIED)) {
            return DENIED;
        }
        if (!productDetailsService.changeProductCost(productName, productCost)) {
            return PRODUCT_NOT_EXIST;
        }
        return SUCCESS;
    }

    public Object addShoppingCart(String listProduct) {
        if (!productDetailsService.addShoppingCart(listProduct)) {
            return PRODUCT_NOT_EXIST;
        }
        return SUCCESS;
    }

    public Object payShoppingCart(String code, int billId) {
        if (!productDetailsService.pay(code, billId)) {
            return "CANT DO THAT NOW";
        }
        return SUCCESS;
    }

    public ShoppingCartHistory getHistory(){
        return productDetailsService.getShoppingCartHistory();
    }

    public boolean addCustomer(String email,String userName, String password){
        if(customerService.addCustomer(email,userName,password)){
            return true;
        }return false;
    }
}

