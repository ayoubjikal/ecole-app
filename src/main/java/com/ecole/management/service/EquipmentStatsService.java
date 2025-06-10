package com.ecole.management.service;

import com.ecole.management.model.EquipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EquipmentStatsService {

    private final EquipmentService equipmentService;
    private final SuppressionService suppressionService;

    @Transactional(readOnly = true)
    public Map<String, Long> getEquipmentStats() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("total", (long) equipmentService.getAllEquipments().size());
        stats.put("active", equipmentService.countActiveEquipments());
        stats.put("supprime", equipmentService.countSupprimedEquipments());
        stats.put("suppressions", suppressionService.countSuppressions());

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getEquipmentStatsByEtablissement(String etablissement) {
        Map<String, Long> stats = new HashMap<>();

        long total = equipmentService.getEquipmentsByEtablissement(etablissement).size();
        long active = equipmentService.getActiveEquipmentsByEtablissement(etablissement).size();
        long supprime = total - active;
        long suppressions = suppressionService.getSuppressionsByEtablissement(etablissement).size();

        stats.put("total", total);
        stats.put("active", active);
        stats.put("supprime", supprime);
        stats.put("suppressions", suppressions);

        return stats;
    }
}