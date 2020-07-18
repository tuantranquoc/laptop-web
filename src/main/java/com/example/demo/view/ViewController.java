package com.example.demo.view;


import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserDTOCreate;
import com.example.demo.dto.UserDTOLogin;
import com.example.demo.model.Laptop;
import com.example.demo.model.Product;
import com.example.demo.repository.LapRepository;
import com.example.demo.service.ProductDetailsService;
import com.example.demo.service.ShoppingCartDetailService;
import com.example.demo.service.UserControllerDTO;
import com.example.demo.service.UserDetailsService;
import com.example.demo.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.demo.note.StaticVariables.LOGIN_FAILURE;
import static com.example.demo.note.StaticVariables.LOGIN_SUCCESS;

@Controller
@RequestMapping(value = "/app")
public class ViewController {

    private final UserControllerDTO userControllerDTO;
    private final LapRepository lapRepository;
    private final ShoppingCartDetailService shoppingCartDetailService;
    private final ProductDetailsService productDetailsService;
    public ViewController(UserDetailsService userDetailsService, UserControllerDTO userControllerDTO, LapRepository lapRepository, ShoppingCartDetailService shoppingCartDetailService, ProductDetailsService productDetailsService) {
        this.userControllerDTO = userControllerDTO;
        this.lapRepository = lapRepository;
        this.shoppingCartDetailService = shoppingCartDetailService;
        this.productDetailsService = productDetailsService;
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String hay(){
        return "LoginPage";
    }

    @RequestMapping(value = "/login/login.html", method = RequestMethod.POST)
    public ModelAndView login01(HttpSession session, @RequestParam(value = "userName") String userName,
                                @RequestParam(value = "password") String password) {
        UserDTOLogin userDTOLogin = new UserDTOLogin();
        userDTOLogin.setUserName(userName);
        userDTOLogin.setPassword(password);
        ModelAndView modelAndView = new ModelAndView("MainPage");
        String status = userControllerDTO.login(session,userDTOLogin);
        List<Laptop> laptops = lapRepository.findAll();
        modelAndView.addObject("laptops",laptops);
        modelAndView.addObject("message","/images/giphy.gif");
        modelAndView.addObject("success",status);
        if(status.equals(LOGIN_SUCCESS)){
        return modelAndView;
        }
        modelAndView.setViewName("LoginFailure");
        return modelAndView;
    }

    @RequestMapping(value = "/service",method = RequestMethod.GET)
    public String hay01(){
        return "Service";
    }

    @RequestMapping(value = "/service/findAllUser",method = RequestMethod.GET)
    public ModelAndView getAllUserInfo(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("MainPage");
        List<UserDTO> userDTOList = userControllerDTO.findAllUser(session);
        List<Product> products = userControllerDTO.findAllProduct();
        List<Laptop> laptops = lapRepository.findAll();
        modelAndView.addObject("laptops",laptops);
        modelAndView.addObject("users",userDTOList);
        modelAndView.addObject("Products",products);
        modelAndView.addObject("message","/images/giphy.gif");
        return modelAndView;
    }


    public ModelAndView findAllProduct(){
        ModelAndView modelAndView = new ModelAndView("MainPage");
        modelAndView.addObject("products",userControllerDTO.findAllProduct());
        return modelAndView;
    }


    public ModelAndView findCurrent(@RequestParam(value = "userId") int userId,
                                    HttpSession session){
        ModelAndView modelAndView = new ModelAndView("MainPage");
        modelAndView.addObject("users",userControllerDTO.findOneUser(userId,session));
        return modelAndView;
    }

    public ModelAndView changeUserName(@RequestParam(value = "userName") String userName){
        ModelAndView modelAndView = new ModelAndView("");
        UserDTOLogin userDTOLogin = new UserDTOLogin();
        userDTOLogin.setUserName(userName);
        modelAndView.addObject("status",userControllerDTO.changeUserName(userDTOLogin));
        return modelAndView;
    }

    @RequestMapping("/")
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("msg","mess");
        return modelAndView;
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String homePage(){
        return "HomePage";
    }

    @RequestMapping(value = "/service/test",method = RequestMethod.GET)
    public ModelAndView getAll(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("test");
        List<UserDTO> userDTOList = userControllerDTO.findAllUser(session);
        List<Product> products = userControllerDTO.findAllProduct();
        modelAndView.addObject("users",userDTOList);
        modelAndView.addObject("products",products);
        modelAndView.addObject("message","/images/giphy.gif");
        return modelAndView;
    }

    @RequestMapping(value = "/service/test/shoppingCart")
    public ModelAndView shoppingCart(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("ShoppingCart");
        List<Product> products = userControllerDTO.findAllProduct();

        modelAndView.addObject("products",products);
        modelAndView.addObject("message","/images/giphy.gif");
        return modelAndView;
    }

    @RequestMapping(value = "/service/test/contact")
    public ModelAndView Contact(){
        ModelAndView modelAndView = new ModelAndView("ContactForm");
        modelAndView.addObject("laptops",lapRepository.findAll());
        return modelAndView;
    }

    @RequestMapping(value = "/service/product/{productId}", method = RequestMethod.GET)
    public ModelAndView product(@PathVariable(name = "productId") int productId){
        ModelAndView modelAndView = new ModelAndView("DisplayProducts");
        Laptop laptop = lapRepository.getOne(productId);
        List<Laptop> laptops = lapRepository.findAll();
        modelAndView.addObject("laptops",laptops);
        modelAndView.addObject("laptop",laptop);
        return modelAndView;
    }

    @RequestMapping(value = "/service/product")
    public ModelAndView productPage(){
        ModelAndView modelAndView = new ModelAndView("LaptopPage");
        List<Laptop> laptops = lapRepository.findAll();
        modelAndView.addObject("laptops",laptops);
        return modelAndView;

    }


    @RequestMapping(value = "/service/shoppingCart/{productId}", method = RequestMethod.GET)
    public ModelAndView addShoppingCart(@PathVariable(name = "productId") int productId ,HttpSession session){
        if(Session.findById(session.getId())!=null){
            ModelAndView modelAndView = new ModelAndView("ShoppingCartPage");
            AtomicReference<String> userName = new AtomicReference<>(Session.findById(session.getId()));
            shoppingCartDetailService.addShoppingCart(userName.get(),productId);
            List<Laptop> laptops = shoppingCartDetailService.laptops(userName.get());
            modelAndView.addObject("laptops",laptops);
            return modelAndView;
        }
        return new ModelAndView("LoginPage");
    }

    @RequestMapping(value = "/service/remove/{productId}")
    public ModelAndView removeShoppingCart(HttpSession session, @PathVariable(name = "productId") int productId){
        if(Session.findById(session.getId())!=null){
            String userName = Session.findById(session.getId());
            shoppingCartDetailService.removeShoppingCart(userName,productId);
            ModelAndView modelAndView = new ModelAndView("ShoppingCartPage");
            List<Laptop> laptopList = shoppingCartDetailService.laptops(userName);
            modelAndView.addObject("laptops",laptopList);
            return modelAndView;
        }
        return new ModelAndView("ShoppingCartPage");
    }

    @RequestMapping(value = "/service/shoppingCart",method = RequestMethod.GET)
    public ModelAndView shoppingCart01(HttpSession session){
        if(Session.findById(session.getId())!=null){
            ModelAndView modelAndView = new ModelAndView("ShoppingCartPage");
            String userName = Session.findById(session.getId());
            List<Laptop> laptopList = shoppingCartDetailService.laptops(userName);
            modelAndView.addObject("laptops",laptopList);
            return modelAndView;
        }
        return new ModelAndView("LoginPage");
    }


    @RequestMapping(value = "/service/signUp",method = RequestMethod.POST)
    public ModelAndView signUp(@RequestParam(name = "userName") String userName, @RequestParam(name = "password") String password
    , @RequestParam(name = "mail") String mail){
        UserDTOCreate userDTOCreate = new UserDTOCreate();
        userDTOCreate.add(userName,password,mail);
        userControllerDTO.signUp(userDTOCreate);
        ModelAndView modelAndView = new ModelAndView("MainPage");
        modelAndView.addObject("laptops",lapRepository.findAll());
        return modelAndView;
    }

    private static boolean dellFlag = false;
    private static boolean asusFlag = false;

    @RequestMapping(value = "/service/product/sort/{brand}",method = RequestMethod.GET)
    public ModelAndView count(@PathVariable(name = "brand") String brand){
        ModelAndView modelAndView = new ModelAndView("LaptopPage");
        List<Laptop> laptops = productDetailsService.findByBrand(brand);
        modelAndView.addObject("laptops",laptops);
        if(brand.equals("dell")) {
            modelAndView.addObject("dellFlag", !dellFlag);
            dellFlag = !dellFlag;
        }
        if(brand.equals("asus")){
            modelAndView.addObject("asusFlag",!asusFlag);
        asusFlag = !asusFlag;}
        return modelAndView;
    }

    @RequestMapping(value = "/service/product/sort/type/{type}",method = RequestMethod.GET)
    public ModelAndView count01(@PathVariable(name = "type") int type){
        ModelAndView modelAndView = new ModelAndView("LaptopPage");
        List<Laptop> laptops =  productDetailsService.findByCost(type);
        System.out.println(laptops.size());
        modelAndView.addObject("laptops",laptops);
        return modelAndView;
    }

    @RequestMapping(value = "/service/product/sort/{type}/{brand}",method = RequestMethod.GET)
    public ModelAndView count02(@PathVariable(name = "type") int type,@PathVariable(name = "brand") String brand){
        ModelAndView modelAndView = new ModelAndView("LaptopPage");
        List<Laptop> laptops =  productDetailsService.findByBrandAndCost(type,brand);
        modelAndView.addObject("laptops",laptops);
        return modelAndView;
    }


    @RequestMapping(value = "/service/pay", method = RequestMethod.POST)
    public ModelAndView login02(HttpSession session, @RequestParam(value = "userName") String userName,
                                @RequestParam(value = "password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        UserDTOLogin userDTOLogin = new UserDTOLogin();
        userDTOLogin.add(userName,password);
        String status = userControllerDTO.login(session,userDTOLogin);
        if(status.equals(LOGIN_FAILURE)){
            modelAndView.setViewName("ShoppingCartPage");
           return modelAndView;
        }
        else{
            shoppingCartDetailService.removeAll();
            return new ModelAndView("ShoppingCartPage");
        }

    }

    @RequestMapping(value = "/service/search",method = RequestMethod.GET)
    public ModelAndView search(@RequestParam String text){
        ModelAndView modelAndView = new ModelAndView("LaptopPage");
        modelAndView.addObject("laptops",productDetailsService.findBy(text));
        return modelAndView;
    }
}







