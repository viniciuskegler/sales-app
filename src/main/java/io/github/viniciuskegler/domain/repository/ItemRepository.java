package io.github.viniciuskegler.domain.repository;

import io.github.viniciuskegler.domain.entity.Client;
import io.github.viniciuskegler.domain.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
