package com.ecole.management.service;

import com.ecole.management.model.InfoEcole;
import com.ecole.management.model.User;
import com.ecole.management.repository.InfoEcoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoEcoleService {

    private final InfoEcoleRepository infoEcoleRepository;

    public List<InfoEcole> getAllInfoEcoles() {
        return infoEcoleRepository.findAll();
    }

    public Optional<InfoEcole> getInfoEcoleByEtablissement(String etablissement) {
        return infoEcoleRepository.findById(etablissement);
    }

    public InfoEcole saveInfoEcole(InfoEcole infoEcole) {
        return infoEcoleRepository.save(infoEcole);
    }

    public void deleteInfoEcole(String etablissement) {
        infoEcoleRepository.deleteById(etablissement);
    }

    public Optional<InfoEcole> getInfoEcoleByUser(User user) {
        return infoEcoleRepository.findAll().stream()
                .filter(ecole -> ecole.getUser() != null && ecole.getUser().getId().equals(user.getId()))
                .findFirst();
    }

    public boolean userHasEcole(User user) {
        return getInfoEcoleByUser(user).isPresent();
    }

    public InfoEcole saveInfoEcoleForUser(InfoEcole infoEcole, User user) {
        // Vérifier que l'utilisateur n'a pas déjà une école
        if (userHasEcole(user)) {
            throw new RuntimeException("Cet utilisateur a déjà créé une école");
        }

        infoEcole.setUser(user);
        return infoEcoleRepository.save(infoEcole);
    }

    // Méthode pour récupérer les écoles accessibles à l'utilisateur connecté
    public List<InfoEcole> getEcolesForCurrentUser(User currentUser) {
        if (currentUser == null) {
            return List.of(); // Retourner liste vide si pas d'utilisateur connecté
        }

        // Pour l'instant, un utilisateur ne voit que sa propre école
        Optional<InfoEcole> userEcole = getInfoEcoleByUser(currentUser);
        return userEcole.map(List::of).orElse(List.of());
    }
}