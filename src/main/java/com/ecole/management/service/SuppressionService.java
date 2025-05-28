package com.ecole.management.service;

import com.ecole.management.model.Suppression;
import com.ecole.management.repository.SuppressionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuppressionService {

    private final SuppressionRepository suppressionRepository;

    public List<Suppression> getAllSuppressions() {
        return suppressionRepository.findAll();
    }

    public List<Suppression> getSuppressionsByEtablissement(String etablissement) {
        return suppressionRepository.findByEtablissement(etablissement);
    }

    public Optional<Suppression> getSuppressionById(Integer id) {
        return suppressionRepository.findById(id);
    }

    public Suppression saveSuppression(Suppression suppression) {
        return suppressionRepository.save(suppression);
    }

    public void deleteSuppression(Integer id) {
        suppressionRepository.deleteById(id);
    }
}