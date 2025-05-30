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

    @NotNull(message = "Le nombre est requis")
    @Min(value = 1, message = "Le nombre doit être au moins 1")
    private Integer nbr;

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
        if (this.nbr != null && this.prix_unitaire != null) {
            this.somme = this.nbr * this.prix_unitaire;
        } else {
            this.somme = 0.0;
        }
    }
}