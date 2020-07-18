package com.example.demo.repository;

import com.example.demo.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill,Integer> {
    Bill findByBillId(int billId);
    Bill findByBillIdAndStatus(int billId, String status);
    Bill findByUserIdAndStatus(int userId, String status);
    List<Bill> findBillsByUserId(int userId);
}
