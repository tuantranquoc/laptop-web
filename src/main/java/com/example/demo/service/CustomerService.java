package com.example.demo.service;


import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Boolean addCustomer(String email, String userName, String password){
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setUserName(userName);
        customer.setPassword(password);
        customer.setFund(1000);
        customerRepository.save(customer);
        return true;
    }
}
