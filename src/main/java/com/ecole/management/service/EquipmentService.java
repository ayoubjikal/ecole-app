package com.ecole.management.service;

import com.ecole.management.model.Equipment;
import com.ecole.management.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipments() {
        List<Equipment> equipments = equipmentRepository.findAll();
        // Ensure all equipment have their sum calculated
        equipments.forEach(equipment -> {
            if (equipment.getSomme() == null && equipment.getNbr() != null && equipment.getPrix_unitaire() != null) {
                equipment.setSomme(equipment.getNbr() * equipment.getPrix_unitaire());
            }
        });
        return equipments;
    }

    public List<Equipment> getEquipmentsByEtablissement(String etablissement) {
        return equipmentRepository.findByEtablissement(etablissement);
    }

    public Optional<Equipment> getEquipmentById(Integer id) {
        return equipmentRepository.findById(id);
    }

    public Equipment saveEquipment(Equipment equipment) {
        // Calculate sum before saving
        if (equipment.getNbr() != null && equipment.getPrix_unitaire() != null) {
            equipment.setSomme(equipment.getNbr() * equipment.getPrix_unitaire());
        }
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipment(Integer id) {
        equipmentRepository.deleteById(id);
    }
}