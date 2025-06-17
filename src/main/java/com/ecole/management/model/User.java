package com.ecole.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est requis")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "Le nom est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Le prénom est requis")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String lastName;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Relation avec InfoEcole - Un utilisateur peut avoir une seule école
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InfoEcole ecole;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}