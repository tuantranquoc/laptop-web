package com.example.demo.repository;

import com.example.demo.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {
    int countAllByUserId(int userId);
    List<UserRole> findAllByUserId(int userId);
    UserRole findFirstByUserId(int userId);
}
