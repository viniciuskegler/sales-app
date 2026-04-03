package com.viniciuskegler.salesapp.mapper;

import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.customer.dto.CustomerDetailsDTO;
import com.viniciuskegler.salesapp.customer.dto.mapper.CustomerMapper;
import com.viniciuskegler.salesapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerMapper();
    }

    @Test
    void toCustomerDetailsDTO_withUser_mapsAllFields() {
        User user = new User();
        user.setEmail("alice@example.com");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Alice");
        customer.setLastName("Wonderland");
        customer.setPhoneNumber("5551234567");
        customer.setUser(user);

        CustomerDetailsDTO dto = mapper.toCustomerDetailsDTO(customer);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Alice Wonderland", dto.fullName());
        assertEquals("alice@example.com", dto.email());
        assertEquals("5551234567", dto.phone());
    }

    @Test
    void toCustomerDetailsDTO_withoutUser_emailIsNull() {
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setFirstName("Bob");
        customer.setLastName("Builder");
        customer.setPhoneNumber("5559876543");
        customer.setUser(null);

        CustomerDetailsDTO dto = mapper.toCustomerDetailsDTO(customer);

        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals("Bob Builder", dto.fullName());
        assertNull(dto.email());
        assertEquals("5559876543", dto.phone());
    }
}
