package com.ecole.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 500, nullable = false)
    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    @Size(max = 500, message = "L'email ne doit pas dépasser 500 caractères")
    private String email;

    @Column(length = 255, nullable = false)
    @NotBlank(message = "Le mot de passe est requis")
    private String password;

    @Column(name = "first_name", length = 300)
    @Size(max = 300, message = "Le prénom ne doit pas dépasser 300 caractères")
    private String firstName;

    @Column(name = "last_name", length = 300)
    @Size(max = 300, message = "Le nom ne doit pas dépasser 300 caractères")
    private String lastName;

    @Column(length = 50)
    @Pattern(regexp = "^[0-9\\+\\-\\s\\(\\)]*$", message = "Format de téléphone invalide")
    @Size(max = 50, message = "Le téléphone ne doit pas dépasser 50 caractères")
    private String telephone;

    @Column(length = 1000)
    @Size(max = 1000, message = "L'adresse ne doit pas dépasser 1000 caractères")
    private String adresse;

    @Column(name = "date_naissance")
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    @Column(length = 100)
    @Size(max = 100, message = "La profession ne doit pas dépasser 100 caractères")
    private String profession;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Column(length = 2000)
    @Size(max = 2000, message = "Les notes ne doivent pas dépasser 2000 caractères")
    private String notes;

    // Relation avec InfoEcole
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private InfoEcole infoEcole;

    // Méthodes UserDetails (pour Spring Security)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Pas de rôles pour l'instant
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive != null && isActive;
    }

    // Méthodes utilitaires
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return email;
        }
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}