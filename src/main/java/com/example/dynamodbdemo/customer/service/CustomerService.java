package com.example.dynamodbdemo.customer.service;

import com.example.dynamodbdemo.customer.domain.Customer;
import com.example.dynamodbdemo.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    public Customer saveCustomer(Customer customer) {
        // 저장하기 전에 필수 필드들을 초기화
        customer.initializeFields();
        return customerRepository.saveCustomer(customer);
    }

    public Customer getCustomerById(String customerId) {
        return customerRepository.getCustomerById(customerId);
    }

    public String deleteCustomerById(String customerId) {
        return  customerRepository.deleteCustomerById(customerId);
    }

    public String updateCustomer(Customer customer) {
        return customerRepository.updateCustomer(customer);
    }
}
