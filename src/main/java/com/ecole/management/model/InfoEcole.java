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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 800, nullable = false, unique = true)
    @NotBlank(message = "Le nom de l'établissement est requis")
    @Size(max = 800, message = "Le nom de l'établissement ne doit pas dépasser 800 caractères")
    private String etablissement;

    @Column(length = 800)
    @Size(max = 800, message = "Le nom de l'académie ne doit pas dépasser 800 caractères")
    private String academie;

    @Column(length = 1000)
    @Size(max = 1000, message = "Le nom de la direction ne doit pas dépasser 1000 caractères")
    private String direction;

    @Column(length = 800)
    @Size(max = 800, message = "Le nom de la région ne doit pas dépasser 800 caractères")
    private String region;

    @Column(length = 800)
    @Size(max = 800, message = "Le nom de la province ne doit pas dépasser 800 caractères")
    private String province;

    @Column(length = 800)
    @Size(max = 800, message = "Le nom de la commune ne doit pas dépasser 800 caractères")
    private String commune;

    @Column(name = "numero_de_compte", length = 100)
    @Size(max = 100, message = "Le numéro de compte ne doit pas dépasser 100 caractères")
    private String numeroDeCompte;

    @Column(name = "date_de_fondation_ou_renouvellement")
    @Temporal(TemporalType.DATE)
    private Date dateDeFondationOuRenouvellement;

    @Column(name = "nom_du_president", length = 300)
    @Size(max = 300, message = "Le nom du président ne doit pas dépasser 300 caractères")
    private String nomDuPresident;

    @Column(name = "nom_du_tresorier", length = 300)
    @Size(max = 300, message = "Le nom du trésorier ne doit pas dépasser 300 caractères")
    private String nomDuTresorier;

    @Column(length = 50)
    @Pattern(regexp = "^[0-9\\+\\-\\s\\(\\)]*$", message = "Format de téléphone invalide")
    @Size(max = 50, message = "Le numéro de téléphone ne doit pas dépasser 50 caractères")
    private String telephone;

    @Column(length = 1000)
    @Size(max = 1000, message = "L'adresse ne doit pas dépasser 1000 caractères")
    private String adresse;

    // RELATION AVEC USER (Un utilisateur = Une école)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Méthodes utilitaires
    public String getNomComplet() {
        return etablissement + (commune != null ? " - " + commune : "");
    }

    @PrePersist
    @PreUpdate
    private void validateUniqueUserEcole() {
        // Cette validation sera gérée au niveau service
    }
}