package com.ecole.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "info_ecole")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoEcole {

    @Column(length = 400)
    @Size(max = 400, message = "Le nom de l'académie ne doit pas dépasser 400 caractères")
    private String academie;

    @Column(length = 500)
    @Size(max = 500, message = "Le nom de la direction ne doit pas dépasser 500 caractères")
    private String direction;

    @Id
    @Column(length = 500)
    @NotBlank(message = "Le nom de l'établissement est requis")
    @Size(max = 500, message = "Le nom de l'établissement ne doit pas dépasser 500 caractères")
    private String etablissement;

    @Column(name = "numero_de_compte", length = 50)
    @Size(max = 50, message = "Le numéro de compte ne doit pas dépasser 50 caractères")
    private String numeroDeCompte;

    @Column(length = 400)
    @Size(max = 400, message = "Le nom de la commune ne doit pas dépasser 400 caractères")
    private String commune;

    @Column(name = "date_de_fondation_ou_renouvellement")
    @Temporal(TemporalType.DATE)
    private Date dateDeFondationOuRenouvellement;

    @Column(name = "nom_du_president", length = 200)
    @Size(max = 200, message = "Le nom du président ne doit pas dépasser 200 caractères")
    private String nomDuPresident;

    @Column(name = "nom_du_tresorier", length = 200)
    @Size(max = 200, message = "Le nom du trésorier ne doit pas dépasser 200 caractères")
    private String nomDuTresorier;

    @Column(length = 20)
    @Pattern(regexp = "^[0-9\\+\\-\\s\\(\\)]*$", message = "Format de téléphone invalide")
    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String telephone;

    @Column(length = 500)
    @Size(max = 500, message = "L'adresse ne doit pas dépasser 500 caractères")
    private String adresse;

    // NOUVELLE RELATION AVEC USER
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}