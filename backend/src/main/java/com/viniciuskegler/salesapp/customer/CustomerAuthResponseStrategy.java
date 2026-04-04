package com.viniciuskegler.salesapp.customer;

import com.viniciuskegler.salesapp.auth.JwtGeneratorService;
import com.viniciuskegler.salesapp.auth.contracts.AuthResponseStrategy;
import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.user.enums.UserRole;
import com.viniciuskegler.salesapp.user.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomerAuthResponseStrategy implements AuthResponseStrategy<CustomerAuthResponseDTO> {

    private final CustomerRepository customerRepository;
    private final JwtGeneratorService jwtService;

    public CustomerAuthResponseStrategy(CustomerRepository customerRepository, JwtGeneratorService jwtService) {
        this.customerRepository = customerRepository;
        this.jwtService = jwtService;
    }

    @Override
    public UserRole support() {
        return UserRole.CUSTOMER;
    }

    @Override
    public BaseAuthResponseDTO<CustomerAuthResponseDTO> buildResponse(User user, String message) {
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("Customer profile not found for: " + user.getEmail()));

        CustomerAuthResponseDTO dto = new CustomerAuthResponseDTO();
        dto.setId(customer.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setFullName(customer.getFullName());

        BaseAuthResponseDTO<CustomerAuthResponseDTO> response = new BaseAuthResponseDTO<>();
        response.setMessage(message);
        response.setToken(jwtService.generateToken(user, Map.of("fullName", customer.getFullName())));
        response.setUserDetails(dto);
        return response;
    }
}
