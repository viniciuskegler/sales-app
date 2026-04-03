package com.viniciuskegler.salesapp.customer.dto.mapper;

import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.customer.dto.CustomerDetailsDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component

public class CustomerMapper {

    public CustomerDetailsDTO toCustomerDetailsDTO(Customer customer) {
        return new CustomerDetailsDTO(
                customer.getId(),
                customer.getFullName(),
                Objects.nonNull(customer.getUser()) ? customer.getUser().getEmail() : null,
                customer.getPhoneNumber()
        );
    }
}