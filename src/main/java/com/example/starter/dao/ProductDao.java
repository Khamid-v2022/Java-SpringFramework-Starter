package com.example.starter.dao;

import com.example.starter.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    List<Product> findAll();

    List<Product> findByNameContaining(String keyword);

    Optional<Product> findById(Long id);

    Product save(Product product);

    void delete(Product product);

    long count();
}
