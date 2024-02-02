package io.github.viniciuskegler.domain.repository;

import io.github.viniciuskegler.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    List<Client> findByNameLike(String name);

    boolean existsByName(String name);

    @Query(" select c from Client c left join fetch c.orders where c.id = :id")
    Client findClientFetchOrders(@Param("id") Integer id);
}
