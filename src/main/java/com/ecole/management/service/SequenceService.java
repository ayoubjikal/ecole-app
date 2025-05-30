package com.ecole.management.service;

import com.ecole.management.model.SequenceCounter;
import com.ecole.management.repository.SequenceCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SequenceService {

    private final SequenceCounterRepository sequenceCounterRepository;
    private static final String EQUIPMENT_SEQUENCE = "EQUIPMENT_SEQUENCE";

    @Transactional
    public Long getNextEquipmentSequence() {
        SequenceCounter counter = sequenceCounterRepository
                .findByNameForUpdate(EQUIPMENT_SEQUENCE)
                .orElse(new SequenceCounter(EQUIPMENT_SEQUENCE));

        Long nextValue = counter.getNextValue();
        sequenceCounterRepository.save(counter);

        return nextValue;
    }

    @Transactional
    public void initializeSequence() {
        if (!sequenceCounterRepository.existsById(EQUIPMENT_SEQUENCE)) {
            sequenceCounterRepository.save(new SequenceCounter(EQUIPMENT_SEQUENCE, 0L));
        }
    }
}