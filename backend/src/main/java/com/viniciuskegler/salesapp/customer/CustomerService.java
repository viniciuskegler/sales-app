package com.viniciuskegler.salesapp.customer;

import com.viniciuskegler.salesapp.customer.dto.CustomerDetailsDTO;
import com.viniciuskegler.salesapp.customer.dto.mapper.CustomerMapper;
import com.viniciuskegler.salesapp.exception.RecordNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    public CustomerDetailsDTO findById(@NotNull @Positive Long id) {
        return customerRepository.findById(id).map(customerMapper::toCustomerDetailsDTO)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

}
