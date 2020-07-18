package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ShoppingCartInfo;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import static com.example.demo.note.StaticVariables.*;
import static com.example.demo.note.StaticVariables.GET;


@Service
public final class ModelMapper {

    UserDTO mapUser(User user, String role){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setRole(role);
        userDTO.addLink(HOST + FIND_CURRENT,SELF);
        userDTO.addLink(HOST + FIND_ALL,GET);
        userDTO.addLink(HOST + FIND_ONE + user.getUserId(),GET);
        return userDTO;
    }

    ProductDTO mapProduct(Product product, String type){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName(product.getProductName());
        productDTO.setProductType(type);
        productDTO.setProductCost(product.getProductCost());
        return productDTO;
    }

    ShoppingCartInfo mapShoppingCart(){
        return new ShoppingCartInfo();
    }

}
