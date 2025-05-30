package com.ecole.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 1)
    @NotBlank(message = "Le symbole est requis")
    @Size(max = 1, message = "Le symbole doit être un seul caractère")
    private String symbol;

    @Column(unique = true, length = 100)
    @NotBlank(message = "Le nom de la catégorie est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String name;

    @Column(length = 500)
    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;
}
