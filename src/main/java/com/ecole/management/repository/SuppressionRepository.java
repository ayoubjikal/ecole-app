package com.ecole.management.repository;

import com.ecole.management.model.Suppression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuppressionRepository extends JpaRepository<Suppression, Integer> {
    List<Suppression> findByEtablissement(String etablissement);
}