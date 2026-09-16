package com.pharmacy.repository;

import com.pharmacy.model.Product;
import com.pharmacy.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContaining(String name);

    List<Product> findByCategory(ProductCategory category);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
