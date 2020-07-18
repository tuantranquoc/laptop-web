package com.example.demo.repository;

import com.example.demo.model.PartyApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyAppRepository extends JpaRepository<PartyApp,Integer> {
    PartyApp findByAppId(int appId);
}
