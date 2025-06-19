package com.ecole.management.service;

import com.ecole.management.model.User;
import com.ecole.management.model.InfoEcole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;
    private final InfoEcoleService infoEcoleService;

    /**
     * Vérifier que l'utilisateur est connecté et a une école
     * @return SecurityCheck object with user info or null if invalid
     */
    public SecurityCheck checkUserAccess() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(currentUser);
        if (!userEcole.isPresent()) {
            return new SecurityCheck(currentUser, null, null);
        }

        return new SecurityCheck(currentUser, userEcole.get(), userEcole.get().getEtablissement());
    }

    /**
     * Vérifier que l'établissement demandé correspond à celui de l'utilisateur
     */
    public boolean isUserAuthorizedForEtablissement(String requestedEtablissement) {
        SecurityCheck check = checkUserAccess();
        if (check == null || check.getUserEtablissement() == null) {
            return false;
        }
        return check.getUserEtablissement().equals(requestedEtablissement);
    }

    public static class SecurityCheck {
        private final User user;
        private final InfoEcole userEcole;
        private final String userEtablissement;

        public SecurityCheck(User user, InfoEcole userEcole, String userEtablissement) {
            this.user = user;
            this.userEcole = userEcole;
            this.userEtablissement = userEtablissement;
        }

        public User getUser() { return user; }
        public InfoEcole getUserEcole() { return userEcole; }
        public String getUserEtablissement() { return userEtablissement; }
        public boolean isValid() { return user != null && userEcole != null; }
        public boolean hasEcole() { return userEcole != null; }
    }
}