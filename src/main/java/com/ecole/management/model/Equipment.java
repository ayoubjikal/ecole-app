package com.ecole.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(unique = true, length = 50)
    @NotBlank(message = "L'ID d'équipement est requis")
    @Size(max = 50, message = "L'ID d'équipement ne doit pas dépasser 50 caractères")
    private String equipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "La catégorie est requise")
    private Category category;

    @Temporal(TemporalType.DATE)
    @NotNull(message = "La date est requise")
    private Date date;

    @Column(length = 2000)
    @NotBlank(message = "La désignation est requise")
    @Size(max = 2000, message = "La désignation ne doit pas dépasser 2000 caractères")
    private String designation;

    @Column(length = 2000)
    @Size(max = 2000, message = "La source ne doit pas dépasser 2000 caractères")
    private String source_equipment;

    @NotNull(message = "Le prix unitaire est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private Double prix_unitaire;

    @Column(length = 1000)
    @Size(max = 1000, message = "La spécialisation ne doit pas dépasser 1000 caractères")
    private String specialisation;

    @Column(length = 500)
    @Size(max = 500, message = "L'état ne doit pas dépasser 500 caractères")
    private String etat;

    @Column(length = 3000)
    @Size(max = 3000, message = "La remarque ne doit pas dépasser 3000 caractères")
    private String remarque;

    @Column(length = 800, nullable = false)
    @NotBlank(message = "L'établissement est requis")
    @Size(max = 800, message = "L'établissement ne doit pas dépasser 800 caractères")
    private String etablissement;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EquipmentStatus status = EquipmentStatus.ACTIVE;

    private Double somme;

    // Relation avec Suppression (optionnelle)
    @OneToOne(mappedBy = "equipment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Suppression suppression;

    // Méthodes utilitaires
    public void markAsSupprime() {
        this.status = EquipmentStatus.SUPPRIME;
    }

    public void reactivate() {
        this.status = EquipmentStatus.ACTIVE;
    }

    public boolean isSupprime() {
        return this.status == EquipmentStatus.SUPPRIME;
    }

    public boolean isActive() {
        return this.status == EquipmentStatus.ACTIVE;
    }

    // Calculer automatiquement la somme
    @PrePersist
    @PreUpdate
    private void calculateSomme() {
        if (this.prix_unitaire != null) {
            this.somme = this.prix_unitaire;
        }
    }
}