package com.example.dynamodbdemo.customer.controller;

import com.example.dynamodbdemo.customer.domain.Customer;
import com.example.dynamodbdemo.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer/insert")
    public Customer saveCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    @GetMapping("/customer/{id}")
    public Customer getCustomerById(@PathVariable("id") String customerId) {
        return customerService.getCustomerById(customerId);
    }

    @GetMapping("/customer/{id}/delete")
    public String deleteCustomerById(@PathVariable("id") String customerId) {
        return  customerService.deleteCustomerById(customerId);
    }

    @GetMapping("/customer/update")
    public String updateCustomer(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }
}