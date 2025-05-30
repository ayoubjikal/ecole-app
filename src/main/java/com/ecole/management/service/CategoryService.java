package com.ecole.management.service;

import com.ecole.management.model.Category;
import com.ecole.management.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryBySymbol(String symbol) {
        return categoryRepository.findBySymbol(symbol);
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category(null, "A", "Fournitures scolaires", "Matériel et équipements scolaires"));
            categoryRepository.save(new Category(null, "B", "Bibliothèque", "Livres et équipements de bibliothèque"));
            categoryRepository.save(new Category(null, "C", "Laboratoire", "Équipements de laboratoire et scientifiques"));
        }
    }
}