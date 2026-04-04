package com.viniciuskegler.salesapp.customer;

import com.viniciuskegler.salesapp.customer.dto.CustomerDetailsDTO;
import com.viniciuskegler.salesapp.customer.dto.mapper.CustomerMapper;
import com.viniciuskegler.salesapp.shared.exception.RecordNotFoundException;
import com.viniciuskegler.salesapp.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerDetailsDTO findByCurrentUser(User currentUser) {
        return customerRepository.findByUser(currentUser)
                .map(customerMapper::toCustomerDetailsDTO)
                .orElseThrow(() -> new RecordNotFoundException(currentUser.getId()));
    }

}
