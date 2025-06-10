package com.ecole.management.repository;


import com.ecole.management.model.Equipment;
import com.ecole.management.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ecole.management.model.EquipmentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    List<Equipment> findByEtablissement(String etablissement);
    List<Equipment> findByCategory(Category category);
    List<Equipment> findByCategoryAndEtablissement(Category category, String etablissement);
    Optional<Equipment> findByEquipmentId(String equipmentId);
    List<Equipment> findByCategoryOrderByEquipmentIdDesc(Category category);

    List<Equipment> findByStatus(EquipmentStatus status);
    List<Equipment> findByStatusAndEtablissement(EquipmentStatus status, String etablissement);
    List<Equipment> findByStatusAndCategory(EquipmentStatus status, Category category);
    List<Equipment> findByStatusAndCategoryAndEtablissement(EquipmentStatus status, Category category, String etablissement);

    @Query("SELECT e FROM Equipment e WHERE e.status = :status ORDER BY e.equipmentId")
    List<Equipment> findActiveEquipmentsOrderByEquipmentId(@Param("status") EquipmentStatus status);

    @Query("SELECT e FROM Equipment e WHERE e.status = :status AND e.etablissement = :etablissement ORDER BY e.equipmentId")
    List<Equipment> findActiveEquipmentsByEtablissementOrderByEquipmentId(@Param("status") EquipmentStatus status, @Param("etablissement") String etablissement);

    // Pagination methods
    Page<Equipment> findByStatus(EquipmentStatus status, Pageable pageable);
    Page<Equipment> findByCategory(Category category, Pageable pageable);
    Page<Equipment> findByEtablissement(String etablissement, Pageable pageable);
    Page<Equipment> findByCategoryAndEtablissement(Category category, String etablissement, Pageable pageable);
    Page<Equipment> findByStatusAndCategory(EquipmentStatus status, Category category, Pageable pageable);
    Page<Equipment> findByStatusAndEtablissement(EquipmentStatus status, String etablissement, Pageable pageable);
    Page<Equipment> findByStatusAndCategoryAndEtablissement(EquipmentStatus status, Category category, String etablissement, Pageable pageable);
}