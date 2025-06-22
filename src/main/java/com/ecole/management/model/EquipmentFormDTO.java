package com.ecole.management.model;

import jakarta.persistence.Temporal;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentFormDTO {

    // NOUVEAU: Champ ID pour les modifications
    private Integer id;

    @Temporal(jakarta.persistence.TemporalType.DATE)
    @NotNull(message = "La date est requise")
    private Date date;

    @NotBlank(message = "La désignation est requise")
    @Size(max = 2000, message = "La désignation ne doit pas dépasser 2000 caractères")
    private String designation;

    @Size(max = 2000, message = "La source ne doit pas dépasser 2000 caractères")
    private String source_equipment;

    @NotNull(message = "La catégorie est requise")
    private Integer categoryId;

    @NotNull(message = "Le nombre est requis")
    @Min(value = 1, message = "Le nombre doit être au moins 1")
    private Integer quantity;

    @NotNull(message = "Le prix unitaire est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private Double prix_unitaire;

    @Size(max = 1000, message = "La spécialisation ne doit pas dépasser 1000 caractères")
    private String specialisation;

    @Size(max = 500, message = "L'état ne doit pas dépasser 500 caractères")
    private String etat;

    @Size(max = 3000, message = "La remarque ne doit pas dépasser 3000 caractères")
    private String remarque;

    @NotBlank(message = "L'établissement est requis")
    @Size(max = 800, message = "Le nom de l'établissement ne doit pas dépasser 800 caractères")
    private String etablissement;

    // NOUVELLE: Méthode pour calculer la somme totale
    public Double getTotalSum() {
        if (quantity != null && prix_unitaire != null) {
            return quantity * prix_unitaire;
        }
        return 0.0;
    }

    // NOUVELLE: Méthode pour convertir en Equipment (CORRECTION ERREUR)
    public Equipment toEquipment() {
        Equipment equipment = new Equipment();
        equipment.setCode(this.id); // Peut être null pour création
        equipment.setDate(this.date);
        equipment.setDesignation(this.designation);
        equipment.setSource_equipment(this.source_equipment);
        equipment.setPrix_unitaire(this.prix_unitaire);
        equipment.setSpecialisation(this.specialisation);
        equipment.setEtat(this.etat);
        equipment.setRemarque(this.remarque);
        equipment.setEtablissement(this.etablissement);
        // La catégorie sera définie séparément dans le contrôleur
        return equipment;
    }

    // NOUVELLE: Constructeur depuis Equipment (pour modifications)
    public static EquipmentFormDTO fromEquipment(Equipment equipment) {
        EquipmentFormDTO dto = new EquipmentFormDTO();
        dto.setId(equipment.getCode());
        dto.setDate(equipment.getDate());
        dto.setDesignation(equipment.getDesignation());
        dto.setSource_equipment(equipment.getSource_equipment());
        dto.setCategoryId(equipment.getCategory() != null ? equipment.getCategory().getId() : null);
        dto.setQuantity(1); // Pour modification, quantité = 1
        dto.setPrix_unitaire(equipment.getPrix_unitaire());
        dto.setSpecialisation(equipment.getSpecialisation());
        dto.setEtat(equipment.getEtat());
        dto.setRemarque(equipment.getRemarque());
        dto.setEtablissement(equipment.getEtablissement());
        return dto;
    }

    // NOUVELLE: Vérifier si c'est une modification
    public boolean isEdit() {
        return this.id != null;
    }
}