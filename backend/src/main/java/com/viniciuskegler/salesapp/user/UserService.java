package com.viniciuskegler.salesapp.user;


import com.viniciuskegler.salesapp.auth.dto.BaseAuthDTO;
import com.viniciuskegler.salesapp.auth.config.JwtGeneratorService;
import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.auth.dto.CustomerAuthResponseDTO;
import com.viniciuskegler.salesapp.auth.dto.CustomerRegisterRequestDTO;
import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.customer.CustomerRepository;
import com.viniciuskegler.salesapp.user.enums.UserRole;
import com.viniciuskegler.salesapp.user.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;



@Validated
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtGeneratorService jwtService;
    private final AuthenticationManager authenticationManager;


    public UserService(UserRepository userRepository, CustomerRepository customerRepository, BCryptPasswordEncoder
                               bCryptPasswordEncoder, JwtGeneratorService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public BaseAuthResponseDTO<CustomerAuthResponseDTO> login(BaseAuthDTO login) {
        User user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + login.getEmail()));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );

        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("Customer profile not found for user: " + login.getEmail()));

        return buildCustomerAuthResponseDTO(user, customer, "Login successful");
    }

    @Transactional
    public BaseAuthResponseDTO<CustomerAuthResponseDTO> registerCustomer(CustomerRegisterRequestDTO customerInfo) {
        User user = new User();
        user.setEmail(customerInfo.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(customerInfo.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setFirstName(customerInfo.getFirstName());
        customer.setLastName(customerInfo.getLastName());
        customer.setPhoneNumber(customerInfo.getPhoneNumber());
        customer.setUser(savedUser);
        Customer savedCustomer = customerRepository.save(customer);
        return buildCustomerAuthResponseDTO(savedUser, savedCustomer, "Customer registered successfully");
    }

    private BaseAuthResponseDTO<CustomerAuthResponseDTO> buildCustomerAuthResponseDTO(User user, Customer customer, String message) {
        BaseAuthResponseDTO<CustomerAuthResponseDTO> responseDTO = new BaseAuthResponseDTO<>();
        responseDTO.setMessage(message);
        responseDTO.setToken(jwtService.generateToken(user));
        CustomerAuthResponseDTO customerResponseDTO = new CustomerAuthResponseDTO();
        customerResponseDTO.setId(customer.getId());
        customerResponseDTO.setRole(user.getRole());
        customerResponseDTO.setEmail(user.getEmail());
        customerResponseDTO.setFullName(customer.getFullName());
        responseDTO.setUserDetails(customerResponseDTO);
        return responseDTO;
    }

}
