package com.example.demo.service;


import com.example.demo.model.Laptop;
import com.example.demo.model.ShoppingCart;
import com.example.demo.repository.LapRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ShoppingCartRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartDetailService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final LapRepository lapRepository;


    public ShoppingCartDetailService(ProductRepository productRepository, UserRepository userRepository, ShoppingCartRepository shoppingCartRepository, LapRepository lapRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.lapRepository = lapRepository;
    }

    public void addShoppingCart(String userName, int productId){
        int userId = userRepository.findByUserName(userName).getUserId();
        if(numberCheck(userId) && duplicate(userId,productId)) {
            double productCost = lapRepository.findByLaptopId(productId).getCost();

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setNumber(1);
            shoppingCart.setTotalCost(productCost);
            shoppingCart.setStatus("process");
            shoppingCart.setProductId(productId);
            shoppingCartRepository.save(shoppingCart);
        }
    }

    public Boolean numberCheck(int userId){
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAllByUserId(userId);
        if(shoppingCarts.size()==3){
            return false;}
        return true;
    }

    public Boolean duplicate(int userId, int productId){
        if(shoppingCartRepository.findByUserIdAndProductId(userId,productId)==null){
            return true;
        }
        return false;
    }

    public Boolean removeShoppingCart(String userName, int productId){
        int userId = userRepository.findByUserName(userName).getUserId();
        if(shoppingCartRepository.findByUserIdAndProductId(userId,productId)!=null){
            shoppingCartRepository.delete(shoppingCartRepository.findByUserIdAndProductId(userId,productId));
            return true;
        }
        return false;
    }

    public List<Laptop> laptops(String userName){
        int userId = userRepository.findByUserName(userName).getUserId();
        List<Laptop> laptops = new ArrayList<>();
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAllByUserId(userId);
        for (ShoppingCart shoppingcart:shoppingCarts
             ) {
            laptops.add(lapRepository.findByLaptopId(shoppingcart.getProductId()));
        }
        return laptops;
    }

    public void removeAll(){
        shoppingCartRepository.deleteAll();
    }
}
