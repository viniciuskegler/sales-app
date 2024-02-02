package io.github.viniciuskegler.domain.repository;

import io.github.viniciuskegler.domain.entity.Client;
import io.github.viniciuskegler.domain.entity.Item;
import io.github.viniciuskegler.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByClient(Client c);
}
