package com.viniciuskegler.salesapp.customer;

import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser(User user);
}
