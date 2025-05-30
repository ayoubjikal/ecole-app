package com.ecole.management.repository;


import com.ecole.management.model.Equipment;
import com.ecole.management.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    List<Equipment> findByEtablissement(String etablissement);
    List<Equipment> findByCategory(Category category);
    List<Equipment> findByCategoryAndEtablissement(Category category, String etablissement);
    Optional<Equipment> findByEquipmentId(String equipmentId);
    List<Equipment> findByCategoryOrderByEquipmentIdDesc(Category category);
}