package com.example.demo.repository;


import com.example.demo.model.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LapRepository extends JpaRepository<Laptop,Integer> {
    Laptop findByLaptopId(int laptopId);
    List<Laptop> findAllByBrand(String brand);
}
