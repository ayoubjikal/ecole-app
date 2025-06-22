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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_code", nullable = false, unique = true)
    @NotNull(message = "L'équipement est requis")
    private Equipment equipment;

    @Temporal(TemporalType.DATE)
    @NotNull(message = "La date de suppression est requise")
    private Date dateSuppression;

    @Column(length = 2000)
    @NotBlank(message = "Le motif de suppression est requis")
    @Size(max = 2000, message = "Le motif ne doit pas dépasser 2000 caractères")
    private String motifSuppression;

    @Column(length = 5000)
    @Size(max = 5000, message = "Les observations ne doivent pas dépasser 5000 caractères")
    private String observations;

    @Column(length = 500)
    @Size(max = 500, message = "Le nom du responsable ne doit pas dépasser 500 caractères")
    private String responsable;

    // Champs dénormalisés pour faciliter les requêtes
    @Column(name = "equipment_id", length = 50)
    private String equipmentId;

    @Column(length = 2000)
    private String designation;

    @Column(length = 800)
    private String etablissement;

    @NotNull(message = "Le prix unitaire est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private Double prix_unitaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Méthodes utilitaires
    @PrePersist
    @PreUpdate
    private void syncWithEquipment() {
        if (this.equipment != null) {
            this.equipmentId = this.equipment.getEquipmentId();
            this.designation = this.equipment.getDesignation();
            this.etablissement = this.equipment.getEtablissement();
            this.prix_unitaire = this.equipment.getPrix_unitaire();
            this.category = this.equipment.getCategory();
        }
    }
}