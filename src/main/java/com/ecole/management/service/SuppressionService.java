
package com.ecole.management.service;

import com.ecole.management.model.Suppression;
import com.ecole.management.model.Equipment;
import com.ecole.management.model.SuppressionFormDTO;
import com.ecole.management.model.EquipmentStatus;
import com.ecole.management.repository.SuppressionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuppressionService {

    private final SuppressionRepository suppressionRepository;
    private final EquipmentService equipmentService;

    @Transactional(readOnly = true)
    public List<Suppression> getAllSuppressions() {
        return suppressionRepository.findAllOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public List<Suppression> getSuppressionsByEtablissement(String etablissement) {
        return suppressionRepository.findByEtablissementOrderByDateDesc(etablissement);
    }

    @Transactional(readOnly = true)
    public List<Suppression> getSuppressionsByCategory(Integer categoryId) {
        return suppressionRepository.findByCategoryOrderByDateDesc(categoryId);
    }

    @Transactional(readOnly = true)
    public Optional<Suppression> getSuppressionById(Integer id) {
        return suppressionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Suppression> getSuppressionByEquipmentId(Integer equipmentId) {
        return suppressionRepository.findByEquipment_Code(equipmentId);
    }

    @Transactional
    public Suppression createSuppressionFromEquipment(SuppressionFormDTO formDTO) {
        // Get the equipment
        Equipment equipment = equipmentService.getEquipmentById(formDTO.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Équipement non trouvé"));

        // Check if equipment is already supprimé
        if (equipment.getStatus() == EquipmentStatus.SUPPRIME) {
            throw new RuntimeException("Cet équipement est déjà supprimé");
        }

        // Check if suppression already exists for this equipment
        if (suppressionRepository.findByEquipment(equipment).isPresent()) {
            throw new RuntimeException("Une suppression existe déjà pour cet équipement");
        }

        // Mark equipment as supprimé
        equipment.markAsSupprime();
        equipmentService.updateEquipment(equipment);

        // Create suppression
        Suppression suppression = new Suppression();
        suppression.setEquipment(equipment);
        suppression.setDateSuppression(formDTO.getDateSuppression());
        suppression.setMotifSuppression(formDTO.getMotifSuppression());
        suppression.setObservations(formDTO.getObservations());
        suppression.setResponsable(formDTO.getResponsable());

        return suppressionRepository.save(suppression);
    }

    @Transactional
    public Suppression updateSuppression(Suppression suppression) {
        return suppressionRepository.save(suppression);
    }

    @Transactional
    public void deleteSuppression(Integer id) {
        // Find the suppression first
        Suppression suppression = suppressionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suppression non trouvée"));

        // Get equipment details
        Equipment equipment = suppression.getEquipment();
        Integer equipmentId = equipment.getCode();

        // Manual deletion approach
        try {
            // Remove the relationship first
            equipment.setSuppression(null);
            equipmentService.updateEquipment(equipment);

            // Force flush
            suppressionRepository.flush();

            // Delete suppression
            suppressionRepository.delete(suppression);
            suppressionRepository.flush();

            // Reactivate equipment
            equipment.setStatus(EquipmentStatus.ACTIVE);
            equipmentService.updateEquipment(equipment);

            System.out.println("DEBUG - Suppression manually deleted: ID=" + id);

        } catch (Exception e) {
            System.err.println("ERROR deleting suppression: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public boolean isEquipmentSupprime(Integer equipmentId) {
        return suppressionRepository.findByEquipment_Code(equipmentId).isPresent();
    }

    @Transactional(readOnly = true)
    public long countSuppressions() {
        return suppressionRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Equipment> getAvailableEquipmentsForSuppression() {
        return equipmentService.getActiveEquipmentsForSelection();
    }

    @Transactional(readOnly = true)
    public List<Equipment> getAvailableEquipmentsForSuppressionByEtablissement(String etablissement) {
        return equipmentService.getActiveEquipmentsForSelectionByEtablissement(etablissement);
    }
}