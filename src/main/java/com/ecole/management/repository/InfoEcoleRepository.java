package com.ecole.management.repository;

import com.ecole.management.model.InfoEcole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoEcoleRepository extends JpaRepository<InfoEcole, String> {
    // Custom queries can be added here if needed
}