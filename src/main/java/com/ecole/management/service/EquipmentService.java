package com.ecole.management.service;

import com.ecole.management.model.*;
import com.ecole.management.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final SequenceService sequenceService;
    private final InfoEcoleService infoEcoleService;

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

    // Pagination methods
    @Transactional(readOnly = true)
    public Page<Equipment> getAllEquipmentsPaginated(Pageable pageable) {
        return equipmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Equipment> getEquipmentsByStatusPaginated(EquipmentStatus status, Pageable pageable) {
        return equipmentRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Equipment> getEquipmentsByCategoryPaginated(Category category, Pageable pageable) {
        return equipmentRepository.findByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Equipment> getEquipmentsByEtablissementPaginated(String etablissement, Pageable pageable) {
        return equipmentRepository.findByEtablissement(etablissement, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Equipment> getEquipmentsByCategoryAndEtablissementPaginated(Category category, String etablissement, Pageable pageable) {
        return equipmentRepository.findByCategoryAndEtablissement(category, etablissement, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Equipment> getEquipmentsByStatusAndCategoryPaginated(EquipmentStatus status, Category category, Pageable pageable) {
        return equipmentRepository.findByStatusAndCategory(status, category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Equipment> getEquipmentsByStatusAndEtablissementPaginated(EquipmentStatus status, String etablissement, Pageable pageable) {
        return equipmentRepository.findByStatusAndEtablissement(status, etablissement, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Equipment> getEquipmentsByStatusAndCategoryAndEtablissementPaginated(EquipmentStatus status, Category category, String etablissement, Pageable pageable) {
        return equipmentRepository.findByStatusAndCategoryAndEtablissement(status, category, etablissement, pageable);
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

            // Calculate sum (same as unit price for single item)
            equipment.setSomme(equipmentTemplate.getPrix_unitaire());

            // Set initial status
            equipment.setStatus(EquipmentStatus.ACTIVE);

            // Save equipment
            Equipment saved = equipmentRepository.save(equipment);
            createdEquipments.add(saved);
        }

        return createdEquipments;
    }

    @Transactional
    public Equipment updateEquipment(Equipment equipment) {
        // Ensure somme is updated when prix_unitaire changes
        if (equipment.getPrix_unitaire() != null) {
            equipment.setSomme(equipment.getPrix_unitaire());
        }
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void deleteEquipment(Integer id) {
        equipmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentsByStatus(EquipmentStatus status) {
        return equipmentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getActiveEquipments() {
        return equipmentRepository.findActiveEquipmentsOrderByEquipmentId(EquipmentStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getActiveEquipmentsByEtablissement(String etablissement) {
        return equipmentRepository.findActiveEquipmentsByEtablissementOrderByEquipmentId(EquipmentStatus.ACTIVE, etablissement);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getActiveEquipmentsByCategory(Category category) {
        return equipmentRepository.findByStatusAndCategory(EquipmentStatus.ACTIVE, category);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getActiveEquipmentsByCategoryAndEtablissement(Category category, String etablissement) {
        return equipmentRepository.findByStatusAndCategoryAndEtablissement(EquipmentStatus.ACTIVE, category, etablissement);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getSupprimedEquipments() {
        return equipmentRepository.findByStatus(EquipmentStatus.SUPPRIME);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getActiveEquipmentsForSelection() {
        return getActiveEquipments();
    }

    @Transactional(readOnly = true)
    public List<Equipment> getActiveEquipmentsForSelectionByEtablissement(String etablissement) {
        return getActiveEquipmentsByEtablissement(etablissement);
    }

    @Transactional(readOnly = true)
    public long countEquipments() {
        return equipmentRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveEquipments() {
        return equipmentRepository.findByStatus(EquipmentStatus.ACTIVE).size();
    }

    @Transactional(readOnly = true)
    public long countSupprimedEquipments() {
        return equipmentRepository.findByStatus(EquipmentStatus.SUPPRIME).size();
    }

    // Additional methods for print functionality (without pagination at all)
    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentsByStatusAndCategory(EquipmentStatus status, Category category) {
        return equipmentRepository.findByStatusAndCategory(status, category);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentsByStatusAndEtablissement(EquipmentStatus status, String etablissement) {
        return equipmentRepository.findByStatusAndEtablissement(status, etablissement);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentsByStatusAndCategoryAndEtablissement(EquipmentStatus status, Category category, String etablissement) {
        return equipmentRepository.findByStatusAndCategoryAndEtablissement(status, category, etablissement);
    }

    // Méthodes orientées utilisateur
    public List<Equipment> getAllEquipmentsForCurrentUser(User user) {
        if (user == null) return List.of();

        Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(user);
        if (userEcole.isPresent()) {
            return getEquipmentsByEtablissement(userEcole.get().getEtablissement());
        }
        return List.of();
    }

    public Page<Equipment> getAllEquipmentsPaginatedForCurrentUser(User user, Pageable pageable) {
        if (user == null) return Page.empty(pageable);

        Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(user);
        if (userEcole.isPresent()) {
            return getEquipmentsByEtablissementPaginated(userEcole.get().getEtablissement(), pageable);
        }
        return Page.empty(pageable);
    }
}