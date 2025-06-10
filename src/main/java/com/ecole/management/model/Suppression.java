package com.ecole.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "suppression")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suppression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "equipment_id", nullable = false, unique = true)
    @NotNull(message = "L'équipement est requis")
    private Equipment equipment;

    @Temporal(TemporalType.DATE)
    @NotNull(message = "La date de suppression est requise")
    private Date dateSuppression;

    @Column(length = 500)
    @NotBlank(message = "Le motif de suppression est requis")
    @Size(max = 500, message = "Le motif ne doit pas dépasser 500 caractères")
    private String motifSuppression;

    @Column(length = 800)
    @Size(max = 800, message = "Les observations ne doivent pas dépasser 800 caractères")
    private String observations;

    @Column(length = 200)
    @Size(max = 200, message = "Le responsable ne doit pas dépasser 200 caractères")
    private String responsable;

    // Convenience methods to access equipment data
    public String getEquipmentId() {
        return equipment != null ? equipment.getEquipmentId() : null;
    }

    public String getDesignation() {
        return equipment != null ? equipment.getDesignation() : null;
    }

    public String getCategoryName() {
        return equipment != null && equipment.getCategory() != null ?
                equipment.getCategory().getName() : null;
    }

    public String getEtablissement() {
        return equipment != null ? equipment.getEtablissement() : null;
    }

    public Double getPrixUnitaire() {
        return equipment != null ? equipment.getPrix_unitaire() : null;
    }
}