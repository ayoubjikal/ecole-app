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
            categoryRepository.save(new Category(null, "A", "التجهيزات والأدوات المدرسية", "يخصص للتجهيزات والأدوات المدرسية (كراسي- طاولات- مكاتب- مقاعد..)"));
            categoryRepository.save(new Category(null, "B", "الخزانة المدرسية", "للخرالة المدرسية من كتب مدرسية وترفيهية ومجلات ووثائق دراسية."));
            categoryRepository.save(new Category(null, "C", "المختبرات", "خاص بالمختبرات سواء تعلق الأمر بتجهيرات مادة علوم الأرص والحياة أو العلوم الميريائية أو عيرها."));
            categoryRepository.save(new Category(null, "D", "أدوات التعليم العام", "يخصص لأدوات التعليم العام، من أدوات وتجهيزات تعليمية تتعلق بتدريس مختلف مواد التعليمالعام."));
            categoryRepository.save(new Category(null, "I", "مواد و تجهيزات القسم الداخلي", "يحصص للمواد والتجهيزات المتعلقة القسم الداخلي."));



        }
    }
}