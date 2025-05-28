package com.ecole.management.model;

import jakarta.persistence.*;
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
    private Date date;

    @Column(length = 700)
    private String designation;

    @Column(length = 700)
    private String source_equipment;

    private Integer nbr;

    private Double prix_unitaire;

    private Double somme;

    @Column(length = 700)
    private String specialisation;

    @Column(length = 500)
    private String etat;

    @Column(length = 800)
    private String remarque;

    @Column(length = 500)
    private String etablissement;

    @ManyToOne
    @JoinColumn(name = "etablissement", referencedColumnName = "etablissement", insertable = false, updatable = false)
    private InfoEcole infoEcole;
}