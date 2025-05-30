package com.ecole.management.repository;

import com.ecole.management.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findBySymbol(String symbol);
    Optional<Category> findByName(String name);
}
