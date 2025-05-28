package com.ecole.management.service;

import com.ecole.management.model.InfoEcole;
import com.ecole.management.repository.InfoEcoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoEcoleService {

    private final InfoEcoleRepository infoEcoleRepository;

    public List<InfoEcole> getAllInfoEcoles() {
        return infoEcoleRepository.findAll();
    }

    public Optional<InfoEcole> getInfoEcoleByEtablissement(String etablissement) {
        return infoEcoleRepository.findById(etablissement);
    }

    public InfoEcole saveInfoEcole(InfoEcole infoEcole) {
        return infoEcoleRepository.save(infoEcole);
    }

    public void deleteInfoEcole(String etablissement) {
        infoEcoleRepository.deleteById(etablissement);
    }
}