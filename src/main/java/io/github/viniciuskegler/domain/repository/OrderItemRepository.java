package io.github.viniciuskegler.domain.repository;

import io.github.viniciuskegler.domain.entity.Item;
import io.github.viniciuskegler.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
