package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartInfo {
    private int billId;
    private double totalCost;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private List<ProductDTO> list = new ArrayList<>();

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public double getTotalCost() {
        return totalCost;

    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public List<ProductDTO> getList() {
        return list;
    }

    public void setList(List<ProductDTO> list) {
        this.list = list;
    }
}
