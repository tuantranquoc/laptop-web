package com.example.demo.repository;

import com.example.demo.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType,Integer> {
    ProductType findByProductId(int productId);
}
