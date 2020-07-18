package com.example.demo.repository;

import com.example.demo.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Integer> {
    List<ShoppingCart> findShoppingCartsByBillId(int billId);
    List<ShoppingCart> findAllByUserId(int userId);
    ShoppingCart findByUserIdAndProductId(int userId, int productId);
}
