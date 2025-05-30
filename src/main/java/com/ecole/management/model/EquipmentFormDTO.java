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

    @Temporal(jakarta.persistence.TemporalType.DATE)
    @NotNull(message = "La date est requise")
    private Date date;

    @NotBlank(message = "La désignation est requise")
    @Size(max = 700, message = "La désignation ne doit pas dépasser 700 caractères")
    private String designation;

    @Size(max = 700, message = "La source ne doit pas dépasser 700 caractères")
    private String source_equipment;

    @NotNull(message = "La catégorie est requise")
    private Integer categoryId;

    @NotNull(message = "Le nombre est requis")
    @Min(value = 1, message = "Le nombre doit être au moins 1")
    private Integer quantity;

    @NotNull(message = "Le prix unitaire est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private Double prix_unitaire;

    @Size(max = 700, message = "La spécialisation ne doit pas dépasser 700 caractères")
    private String specialisation;

    @Size(max = 500, message = "L'état ne doit pas dépasser 500 caractères")
    private String etat;

    @Size(max = 800, message = "La remarque ne doit pas dépasser 800 caractères")
    private String remarque;

    @NotBlank(message = "L'établissement est requis")
    @Size(max = 500, message = "Le nom de l'établissement ne doit pas dépasser 500 caractères")
    private String etablissement;

    // Convenience method to calculate total sum
    public Double getTotalSum() {
        if (quantity != null && prix_unitaire != null) {
            return quantity * prix_unitaire;
        }
        return 0.0;
    }
}