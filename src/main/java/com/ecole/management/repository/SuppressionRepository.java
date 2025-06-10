package com.ecole.management.repository;

import com.ecole.management.model.Suppression;
import com.ecole.management.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuppressionRepository extends JpaRepository<Suppression, Integer> {

    Optional<Suppression> findByEquipment(Equipment equipment);
    Optional<Suppression> findByEquipment_Code(Integer equipmentCode);

    @Query("SELECT s FROM Suppression s JOIN s.equipment e WHERE e.etablissement = :etablissement ORDER BY s.dateSuppression DESC")
    List<Suppression> findByEtablissementOrderByDateDesc(@Param("etablissement") String etablissement);

    @Query("SELECT s FROM Suppression s JOIN s.equipment e WHERE e.category.id = :categoryId ORDER BY s.dateSuppression DESC")
    List<Suppression> findByCategoryOrderByDateDesc(@Param("categoryId") Integer categoryId);

    @Query("SELECT s FROM Suppression s ORDER BY s.dateSuppression DESC")
    List<Suppression> findAllOrderByDateDesc();
}