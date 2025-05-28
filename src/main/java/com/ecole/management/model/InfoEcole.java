package com.ecole.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "info_ecole")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoEcole {

    @Column(length = 400)
    private String academie;

    @Column(length = 500)
    private String direction;

    @Id
    @Column(length = 500)
    private String etablissement;

    @Column(name = "numero_de_compte", length = 50)
    private String numeroDeCompte;

    @Column(length = 400)
    private String commune;

    @Column(name = "date_de_fondation_ou_renouvellement")
    @Temporal(TemporalType.DATE)
    private Date dateDeFondationOuRenouvellement;

    @Column(name = "nom_du_president", length = 200)
    private String nomDuPresident;

    @Column(name = "nom_du_tresorier", length = 200)
    private String nomDuTresorier;

    @Column(length = 20)
    private String telephone;

    @Column(length = 500)
    private String adresse;

    @OneToMany(mappedBy = "infoEcole", cascade = CascadeType.ALL)
    private List<Equipment> equipments;
}