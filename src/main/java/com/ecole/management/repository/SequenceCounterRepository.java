package com.ecole.management.repository;

import com.ecole.management.model.SequenceCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface SequenceCounterRepository extends JpaRepository<SequenceCounter, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SequenceCounter s WHERE s.name = :name")
    Optional<SequenceCounter> findByNameForUpdate(String name);
}