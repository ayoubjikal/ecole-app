package com.ecole.management.service;

import com.ecole.management.model.InfoEcole;
import com.ecole.management.model.User;
import com.ecole.management.repository.InfoEcoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InfoEcoleService {

    private final InfoEcoleRepository infoEcoleRepository;

    // Récupérer toutes les écoles (pour admin uniquement)
    @Transactional(readOnly = true)
    public List<InfoEcole> getAllInfoEcoles() {
        return infoEcoleRepository.findAll();
    }

    // Récupérer école par ID
    @Transactional(readOnly = true)
    public Optional<InfoEcole> getInfoEcoleById(Long id) {
        return infoEcoleRepository.findById(id);
    }

    // Récupérer école par nom d'établissement (pour compatibilité)
    @Transactional(readOnly = true)
    public Optional<InfoEcole> getInfoEcoleByEtablissement(String etablissement) {
        return infoEcoleRepository.findByEtablissement(etablissement);
    }

    // Récupérer école par utilisateur
    @Transactional(readOnly = true)
    public Optional<InfoEcole> getInfoEcoleByUser(User user) {
        return infoEcoleRepository.findByUser(user);
    }

    // Vérifier si utilisateur a une école
    @Transactional(readOnly = true)
    public boolean userHasEcole(User user) {
        return infoEcoleRepository.existsByUser(user);
    }

    // Créer nouvelle école pour utilisateur
    public InfoEcole createInfoEcoleForUser(InfoEcole infoEcole, User user) {
        // Vérifier que l'utilisateur n'a pas déjà une école
        if (userHasEcole(user)) {
            throw new RuntimeException("Cet utilisateur a déjà créé une école");
        }

        // Vérifier que le nom d'établissement n'existe pas déjà
        if (infoEcoleRepository.existsByEtablissement(infoEcole.getEtablissement())) {
            throw new RuntimeException("Un établissement avec ce nom existe déjà");
        }

        infoEcole.setUser(user);
        return infoEcoleRepository.save(infoEcole);
    }

    // Mettre à jour école existante
    public InfoEcole updateInfoEcole(InfoEcole infoEcole, User user) {
        // Vérifier que l'école appartient à l'utilisateur
        Optional<InfoEcole> existingEcole = getInfoEcoleByUser(user);

        if (!existingEcole.isPresent()) {
            throw new RuntimeException("Aucune école trouvée pour cet utilisateur");
        }

        InfoEcole ecoleToUpdate = existingEcole.get();

        // Vérifier si le nouveau nom d'établissement existe déjà (sauf si c'est le même)
        if (!ecoleToUpdate.getEtablissement().equals(infoEcole.getEtablissement()) &&
                infoEcoleRepository.existsByEtablissement(infoEcole.getEtablissement())) {
            throw new RuntimeException("Un établissement avec ce nom existe déjà");
        }

        // Mettre à jour tous les champs
        ecoleToUpdate.setEtablissement(infoEcole.getEtablissement());
        ecoleToUpdate.setAcademie(infoEcole.getAcademie());
        ecoleToUpdate.setDirection(infoEcole.getDirection());
        ecoleToUpdate.setRegion(infoEcole.getRegion());
        ecoleToUpdate.setProvince(infoEcole.getProvince());
        ecoleToUpdate.setCommune(infoEcole.getCommune());
        ecoleToUpdate.setNumeroDeCompte(infoEcole.getNumeroDeCompte());
        ecoleToUpdate.setDateDeFondationOuRenouvellement(infoEcole.getDateDeFondationOuRenouvellement());
        ecoleToUpdate.setNomDuPresident(infoEcole.getNomDuPresident());
        ecoleToUpdate.setNomDuTresorier(infoEcole.getNomDuTresorier());
        ecoleToUpdate.setTelephone(infoEcole.getTelephone());
        ecoleToUpdate.setAdresse(infoEcole.getAdresse());

        return infoEcoleRepository.save(ecoleToUpdate);
    }

    // Sauvegarder école (logique intelligente)
    public InfoEcole saveInfoEcoleForUser(InfoEcole infoEcole, User user) {
        if (userHasEcole(user)) {
            // Modification d'une école existante
            return updateInfoEcole(infoEcole, user);
        } else {
            // Création d'une nouvelle école
            return createInfoEcoleForUser(infoEcole, user);
        }
    }

    // Supprimer école
    public void deleteInfoEcole(Long id, User user) {
        Optional<InfoEcole> ecole = infoEcoleRepository.findById(id);

        if (!ecole.isPresent()) {
            throw new RuntimeException("École non trouvée");
        }

        // Vérifier que l'école appartient à l'utilisateur
        if (!ecole.get().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cette école");
        }

        infoEcoleRepository.deleteById(id);
    }

    // Récupérer écoles pour utilisateur actuel
    @Transactional(readOnly = true)
    public List<InfoEcole> getEcolesForCurrentUser(User currentUser) {
        if (currentUser == null) {
            return List.of();
        }

        Optional<InfoEcole> userEcole = getInfoEcoleByUser(currentUser);
        return userEcole.map(List::of).orElse(List.of());
    }

    // Récupérer ID de l'école de l'utilisateur
    @Transactional(readOnly = true)
    public Optional<Long> getEcoleIdForUser(User user) {
        return getInfoEcoleByUser(user).map(InfoEcole::getId);
    }

    // Méthode utilitaire pour la migration
    @Transactional(readOnly = true)
    public boolean isEtablissementAvailableForUser(String etablissement, User user) {
        Optional<InfoEcole> existing = infoEcoleRepository.findByEtablissement(etablissement);
        return !existing.isPresent() || existing.get().getUser().getId().equals(user.getId());
    }
}