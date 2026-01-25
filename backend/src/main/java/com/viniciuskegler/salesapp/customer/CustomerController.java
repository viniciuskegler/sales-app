package com.viniciuskegler.salesapp.customer;

import com.viniciuskegler.salesapp.customer.dto.CustomerDetailsDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public CustomerDetailsDTO findById(@PathVariable Long id) {
        return customerService.findById(id);
    }


}
