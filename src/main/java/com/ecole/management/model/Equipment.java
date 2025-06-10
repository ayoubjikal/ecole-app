package com.ecole.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ecole.management.model.EquipmentStatus;


import java.util.Date;


@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;

    @Column(unique = true, length = 20)
    @NotBlank(message = "L'ID d'équipement est requis")
    private String equipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "La catégorie est requise")
    private Category category;

    @Temporal(TemporalType.DATE)
    @NotNull(message = "La date est requise")
    private Date date;

    @Column(length = 700)
    @NotBlank(message = "La désignation est requise")
    @Size(max = 700, message = "La désignation ne doit pas dépasser 700 caractères")
    private String designation;

    @Column(length = 700)
    @Size(max = 700, message = "La source ne doit pas dépasser 700 caractères")
    private String source_equipment;

    // Removed nbr field - each equipment is now a single item
    // Quantity logic handled in service layer during creation

    @NotNull(message = "Le prix unitaire est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private Double prix_unitaire;

    private Double somme;

    @Column(length = 700)
    @Size(max = 700, message = "La spécialisation ne doit pas dépasser 700 caractères")
    private String specialisation;

    @Column(length = 500)
    @Size(max = 500, message = "L'état ne doit pas dépasser 500 caractères")
    private String etat;

    @Column(length = 800)
    @Size(max = 800, message = "La remarque ne doit pas dépasser 800 caractères")
    private String remarque;

    @Column(length = 500)
    @NotBlank(message = "L'établissement est requis")
    @Size(max = 500, message = "Le nom de l'établissement ne doit pas dépasser 500 caractères")
    private String etablissement;

    // Méthode pour calculer automatiquement la somme
    @PrePersist
    @PreUpdate
    public void calculateSomme() {
        if (this.prix_unitaire != null) {
            this.somme = this.prix_unitaire; // Each equipment is a single item now
        } else {
            this.somme = 0.0;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status = EquipmentStatus.ACTIVE;

    @OneToOne(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Suppression suppression;

    // Add helper methods
    public boolean isActive() {
        return status == EquipmentStatus.ACTIVE;
    }

    public boolean isSupprime() {
        return status == EquipmentStatus.SUPPRIME;
    }

    public void markAsSupprime() {
        this.status = EquipmentStatus.SUPPRIME;
    }
}