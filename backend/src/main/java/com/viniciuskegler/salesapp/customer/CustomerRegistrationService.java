package com.viniciuskegler.salesapp.customer;

import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerRegisterRequestDTO;
import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.user.UserService;
import com.viniciuskegler.salesapp.user.enums.UserRole;
import com.viniciuskegler.salesapp.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class CustomerRegistrationService {

    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final CustomerAuthResponseStrategy responseStrategy;

    public CustomerRegistrationService(UserService userService,
                                       CustomerRepository customerRepository,
                                       CustomerAuthResponseStrategy responseStrategy) {
        this.userService = userService;
        this.customerRepository = customerRepository;
        this.responseStrategy = responseStrategy;
    }

    @Transactional
    public BaseAuthResponseDTO<CustomerAuthResponseDTO> register(CustomerRegisterRequestDTO request) {
        User user = userService.createUser(request.getEmail(), request.getPassword(), UserRole.CUSTOMER);

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setUser(user);
        customerRepository.save(customer);

        return responseStrategy.buildResponse(user, "Customer registered successfully");
    }
}
