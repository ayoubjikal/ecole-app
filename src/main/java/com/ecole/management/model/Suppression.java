package com.ecole.management.model;

import jakarta.persistence.*;
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

    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(length = 700)
    private String designation;

    @Column(length = 500)
    private String etablissement;

    private Integer quantite;

    @Column(length = 1000)
    private String motif;

    @Column(length = 500)
    private String remarque;
}