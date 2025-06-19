package com.ecole.management.config;

import com.ecole.management.service.CategoryService;
import com.ecole.management.service.SequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryService categoryService;
    private final SequenceService sequenceService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default categories
        categoryService.initializeDefaultCategories();

        // Initialize sequence counter
        sequenceService.initializeSequence();
    }
}


