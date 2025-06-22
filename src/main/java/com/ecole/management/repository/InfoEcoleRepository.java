package com.ecole.management.repository;

import com.ecole.management.model.InfoEcole;
import com.ecole.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface InfoEcoleRepository extends JpaRepository<InfoEcole, Long> {

    // Trouver par établissement (pour compatibilité)
    Optional<InfoEcole> findByEtablissement(String etablissement);

    // Trouver par utilisateur
    Optional<InfoEcole> findByUser(User user);

    // Vérifier si utilisateur a déjà une école
    boolean existsByUser(User user);

    // Vérifier si établissement existe déjà (pour éviter doublons)
    boolean existsByEtablissement(String etablissement);

    // Trouver par utilisateur avec requête optimisée
    @Query("SELECT e FROM InfoEcole e WHERE e.user.id = :userId")
    Optional<InfoEcole> findByUserId(@Param("userId") Long userId);

    // Récupérer toutes les écoles avec leurs utilisateurs (pour admin)
    @Query("SELECT e FROM InfoEcole e JOIN FETCH e.user")
    List<InfoEcole> findAllWithUsers();

    // Recherche par nom d'établissement (pour recherche)
    @Query("SELECT e FROM InfoEcole e WHERE LOWER(e.etablissement) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<InfoEcole> findByEtablissementContainingIgnoreCase(@Param("name") String name);

    // Pour migration - trouver par ancien ID (établissement)
    @Query("SELECT e FROM InfoEcole e WHERE e.etablissement = :etablissement AND e.user.id = :userId")
    Optional<InfoEcole> findByEtablissementAndUserId(@Param("etablissement") String etablissement, @Param("userId") Long userId);
}