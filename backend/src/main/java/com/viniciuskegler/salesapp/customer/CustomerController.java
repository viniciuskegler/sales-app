package com.viniciuskegler.salesapp.customer;

import com.viniciuskegler.salesapp.customer.dto.CustomerDetailsDTO;
import com.viniciuskegler.salesapp.user.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/me")
    public CustomerDetailsDTO findCurrentCustomer(@AuthenticationPrincipal User currentUser) {
        return customerService.findByCurrentUser(currentUser);
    }

}
