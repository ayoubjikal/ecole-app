package com.ecole.management.service;

import com.ecole.management.model.Equipment;
import com.ecole.management.model.Category;
import com.ecole.management.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final SequenceService sequenceService;

    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentsByEtablissement(String etablissement) {
        return equipmentRepository.findByEtablissement(etablissement);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentsByCategory(Category category) {
        return equipmentRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentsByCategoryAndEtablissement(Category category, String etablissement) {
        return equipmentRepository.findByCategoryAndEtablissement(category, etablissement);
    }

    @Transactional(readOnly = true)
    public Optional<Equipment> getEquipmentById(Integer id) {
        return equipmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Equipment> getEquipmentByEquipmentId(String equipmentId) {
        return equipmentRepository.findByEquipmentId(equipmentId);
    }

    @Transactional
    public List<Equipment> createEquipments(Equipment equipmentTemplate, Integer quantity) {
        List<Equipment> createdEquipments = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            Equipment equipment = new Equipment();

            // Copy properties from template
            equipment.setDate(equipmentTemplate.getDate());
            equipment.setDesignation(equipmentTemplate.getDesignation());
            equipment.setSource_equipment(equipmentTemplate.getSource_equipment());
            equipment.setPrix_unitaire(equipmentTemplate.getPrix_unitaire());
            equipment.setSpecialisation(equipmentTemplate.getSpecialisation());
            equipment.setEtat(equipmentTemplate.getEtat());
            equipment.setRemarque(equipmentTemplate.getRemarque());
            equipment.setEtablissement(equipmentTemplate.getEtablissement());
            equipment.setCategory(equipmentTemplate.getCategory());

            // Generate unique equipment ID
            Long sequence = sequenceService.getNextEquipmentSequence();
            String equipmentId = equipmentTemplate.getCategory().getSymbol() + "-" + sequence;
            equipment.setEquipmentId(equipmentId);

            // Calculate sum (prix_unitaire * 1 since each equipment is now a single item)
            equipment.setSomme(equipmentTemplate.getPrix_unitaire());

            Equipment savedEquipment = equipmentRepository.save(equipment);
            createdEquipments.add(savedEquipment);
        }

        return createdEquipments;
    }

    @Transactional
    public Equipment updateEquipment(Equipment equipment) {
        // Calculate sum before saving
        if (equipment.getPrix_unitaire() != null) {
            equipment.setSomme(equipment.getPrix_unitaire());
        }
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void deleteEquipment(Integer id) {
        equipmentRepository.deleteById(id);
    }
}