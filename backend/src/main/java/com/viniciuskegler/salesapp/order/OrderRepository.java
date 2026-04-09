package com.viniciuskegler.salesapp.order;

import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerOrderByCreatedAtDesc(Customer customer);

    Optional<Order> findByIdAndCustomer(Long id, Customer customer);

    List<Order> findByStatusIn(List<OrderStatus> statuses);
}
