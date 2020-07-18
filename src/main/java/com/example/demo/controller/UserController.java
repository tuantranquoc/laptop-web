package com.example.demo.controller;

import com.example.demo.config.RSAKeyPairGenerator;
import com.example.demo.config.RSAUtil;
import com.example.demo.dto.*;
import com.example.demo.model.Customer;
import com.example.demo.model.Laptop;
import com.example.demo.model.ListLaptop;
import com.example.demo.model.Product;
import com.example.demo.repository.LapRepository;
import com.example.demo.service.ProductDetailsService;
import com.example.demo.service.UserControllerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSession;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping(value = "/app/api")
public class UserController {

    private final UserControllerDTO userControllerDTO;
    private final LapRepository lapRepository;
    private final ProductDetailsService productDetailsService;

    @Autowired
    public UserController(UserControllerDTO userControllerDTO, LapRepository lapRepository, ProductDetailsService productDetailsService) {
        this.userControllerDTO = userControllerDTO;
        this.lapRepository = lapRepository;
        this.productDetailsService = productDetailsService;
    }


    ///// only for testing
    ///////////////////////////////////////////////////

    //Need Company Controller for this one uh hum?

    //Need one service for view controller.

    //only return object here

    // check new app


    //CODE CHECK OAUTH 2.0

    /*@RequestMapping(value = "/web/login/valid/{clientId}/{validId}", method = RequestMethod.GET)
    public Object requestValid(@PathVariable int validId,@PathVariable int clientId){
        return userControllerDTO.response(validId,clientId);
    }*/
    @RequestMapping(value = "web/login/valid/{clientId}/{validId}", method = RequestMethod.GET)
    public Object requestAccess(@PathVariable int validId, @PathVariable int clientId) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException {
        return userControllerDTO.getAuthCode(validId, clientId);
    }

    @RequestMapping(value = "web/login/valid/get-access/{clientId}")
    public Object requestToken(@RequestBody AuthCode authCode, @PathVariable int clientId,HttpSession session) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException {
        return userControllerDTO.response(authCode.getAuthCode(), clientId,session);
    }

    @RequestMapping(value = "/web/login/valid/resource", method = RequestMethod.POST)
    public Object getResource(@RequestBody AccessToken accessToken, HttpSession session) {
        return userControllerDTO.getUserResource(accessToken,session);
    }


    // TRY IT WITH RSA
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
        String publicKey = keyPairGenerator.getPublicKey().toString();
        String privateKey = keyPairGenerator.getPrivateKey().toString();

        String encryptedString = Base64.getEncoder().encodeToString(RSAUtil.encrypt("Susan is the bét", publicKey));
        System.out.println(encryptedString);
        String decryptedString = RSAUtil.decrypt(encryptedString, privateKey);
        System.out.println(decryptedString);

    }

    //TRASH !!!!!!!!

    //DON'T TOUCH ANYTHING DOWN HERE!!!!!
    //////////////////////////////////////////////////////////////////////////////////////////
    /// API support user

    //ADMIN


    //LOGIN
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, @RequestBody UserDTOLogin userDTOLogin) {
        return userControllerDTO.login(session, userDTOLogin);
    }

    //LOGOUT
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        return userControllerDTO.logout(session);
    }

    //SIGN
    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    public String signUp(@RequestBody UserDTOCreate userDTOCreate) {
        return userControllerDTO.signUp(userDTOCreate);
    }

    //FIND ALL USER
    @RequestMapping(value = "/findAll", method = RequestMethod.GET) //check this one!!!
    public List<UserDTO> findAll(HttpSession session) {
        return userControllerDTO.findAllUser(session);
    }

    //FIND ONE USER BY ID
    @RequestMapping(value = "/findOne/{id}", method = RequestMethod.GET)
    public Object findOne(@PathVariable int id, HttpSession session) {
        return userControllerDTO.findOneUser(id, session);
    }

    //FIND THE CURRENT USER
    @RequestMapping(value = "/findCurrent", method = RequestMethod.GET)
    public Object findCurrentUser(HttpSession session) {
        return userControllerDTO.findCurrent(session);
    }

    //DELETE USER BY USER NAME
    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.PUT)
    public Object delete(@PathVariable int userId, HttpSession session) {
        return userControllerDTO.delete(session, userId);
    }

    //SWAP USER ROLE
    @RequestMapping(value = "/swap", method = RequestMethod.GET)
    public Object swapRole(HttpSession session) {
        return userControllerDTO.swapRole(session);
    }

    //ADD THE NEW ROLE FOR USER
    @RequestMapping(value = "/addRole", method = RequestMethod.GET)
    public Object addRole(HttpSession session) {
        return userControllerDTO.addRole(session);
    }

    @RequestMapping(value = "/changeUserName", method = RequestMethod.PUT)
    public Object changUserName(@RequestBody UserDTOLogin userDTOLogin) {
        return userControllerDTO.changeUserName(userDTOLogin);
    }

    ////////////////////////////////////////////////////
    //API method support our Product

    @RequestMapping(value = "/findOneProduct/{productId}", method = RequestMethod.GET)
    public Object findProduct(@PathVariable int productId) {
        return userControllerDTO.findOneProduct(productId);
    }

    @RequestMapping(value = "/findAllProduct", method = RequestMethod.GET)
    public List<Product> findAllProduct() {
        return userControllerDTO.findAllProduct();
    }

    @RequestMapping(value = "/addProduct", method = RequestMethod.PUT)
    public Object addProduct(@RequestBody Product product, HttpSession session) {
        return userControllerDTO.addProduct(session, product);
    }

    @RequestMapping(value = "/changeProductName/{productCurrentName}/{productName}", method = RequestMethod.GET)
    public Object changeProductName(HttpSession session, @PathVariable String productCurrentName, @PathVariable String productName) {
        return userControllerDTO.changeProductName(session, productCurrentName, productName);
    }

    @RequestMapping(value = "changeProductCost/{productName}/{productCost}", method = RequestMethod.GET)
    public Object changeProductCost(HttpSession session, @PathVariable String productName, @PathVariable double productCost) {
        return userControllerDTO.changeProductCost(session, productName, productCost);
    }

    @RequestMapping(value = "addShoppingCart/{listProduct}", method = RequestMethod.PUT)
    public Object addShoppingCart(@PathVariable String listProduct) {
        return userControllerDTO.addShoppingCart(listProduct);
    }

    @RequestMapping(value = "/laptop", method = RequestMethod.GET)
    public List<Laptop> lp(){
        return lapRepository.findAll();
    }
    @RequestMapping(value = "payShoppingCart/{code}/{billId}", method = RequestMethod.GET)
    public Object payShoppingCart(@PathVariable String code, @PathVariable int billId) {
        return userControllerDTO.payShoppingCart(code, billId);
    }

    @RequestMapping(value = "/getHistory",method = RequestMethod.GET)
    public ShoppingCartHistory getHistory(){
        return userControllerDTO.getHistory();
    }

    @RequestMapping(value = "/service/product/sort/{brand}",method = RequestMethod.GET)
    public int count(@PathVariable(name = "brand") String brand){
        return productDetailsService.findByBrand(brand).size();
    }

    @RequestMapping(value = "/service/product/sort/type/{type}",method = RequestMethod.GET)
    public int count01(@PathVariable(name = "type") int type){
        return productDetailsService.findByCost(type).size();
    }

    @RequestMapping(value = "/service/product/sort/{type}/{brand}",method = RequestMethod.GET)
    public int count02(@PathVariable(name = "type") int type,@PathVariable(name = "brand") String brand){
        return productDetailsService.findByBrandAndCost(type,brand).size();
    }


    // TESTING FOR ANDROID!
    @RequestMapping(value = "/addCustomer",method = RequestMethod.POST)
    public Boolean SignUP(@RequestBody Customer customer){
        return userControllerDTO.addCustomer(customer.getEmail(),customer.getUserName(),customer.getPassword());
    }

    @RequestMapping(value = "/test/new",method = RequestMethod.GET)
    public String TEST(){
        System.out.println("OK");
        return "OK";
    }
    @RequestMapping(value = "/test/laptop",method = RequestMethod.GET)
    public ListLaptop laptop(){
        ListLaptop listLaptop = new ListLaptop();
        listLaptop.setList(lapRepository.findAll());
        System.out.println("OK");
        return listLaptop;
    }
}

