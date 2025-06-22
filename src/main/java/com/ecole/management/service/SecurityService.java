package com.ecole.management.service;

import com.ecole.management.model.User;
import com.ecole.management.model.InfoEcole;
import com.ecole.management.model.Equipment;
import com.ecole.management.model.Suppression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    private final UserService userService;
    private final InfoEcoleService infoEcoleService;
    private final EquipmentService equipmentService;
    private final SuppressionService suppressionService;

    /**
     * Vérification complète d'accès utilisateur
     */
    public SecurityCheck checkUserAccess() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("Tentative d'accès sans authentification - IP: {}", getCurrentUserIP());
            return SecurityCheck.unauthorized();
        }

        Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(currentUser);
        if (!userEcole.isPresent()) {
            logger.warn("Utilisateur {} n'a pas d'école", currentUser.getEmail());
            return SecurityCheck.noSchool(currentUser);
        }

        logger.debug("Accès autorisé pour {} - école {}",
                currentUser.getEmail(), userEcole.get().getEtablissement());

        return SecurityCheck.authorized(currentUser, userEcole.get());
    }

    /**
     * Vérifier si l'équipement appartient à l'utilisateur
     */
    public boolean canAccessEquipment(Integer equipmentId, User user) {
        if (user == null || equipmentId == null) {
            return false;
        }

        Optional<Equipment> equipment = equipmentService.getEquipmentById(equipmentId);
        if (!equipment.isPresent()) {
            logger.warn("Tentative d'accès à équipement inexistant {} par {}",
                    equipmentId, user.getEmail());
            return false;
        }

        Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(user);
        if (!userEcole.isPresent()) {
            return false;
        }

        boolean hasAccess = userEcole.get().getEtablissement().equals(equipment.get().getEtablissement());

        if (!hasAccess) {
            logger.warn("SÉCURITÉ: Utilisateur {} a tenté d'accéder à l'équipement {} d'un autre établissement",
                    user.getEmail(), equipmentId);
        }

        return hasAccess;
    }

    /**
     * Vérifier si la suppression appartient à l'utilisateur
     */
    public boolean canAccessSuppression(Integer suppressionId, User user) {
        if (user == null || suppressionId == null) {
            return false;
        }

        Optional<Suppression> suppression = suppressionService.getSuppressionById(suppressionId);
        if (!suppression.isPresent()) {
            logger.warn("Tentative d'accès à suppression inexistante {} par {}",
                    suppressionId, user.getEmail());
            return false;
        }

        Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(user);
        if (!userEcole.isPresent()) {
            return false;
        }

        boolean hasAccess = userEcole.get().getEtablissement().equals(suppression.get().getEtablissement());

        if (!hasAccess) {
            logger.warn("SÉCURITÉ: Utilisateur {} a tenté d'accéder à la suppression {} d'un autre établissement",
                    user.getEmail(), suppressionId);
        }

        return hasAccess;
    }

    /**
     * Vérifier si l'école appartient à l'utilisateur
     */
    public boolean canAccessEcole(Long ecoleId, User user) {
        if (user == null || ecoleId == null) {
            return false;
        }

        Optional<InfoEcole> ecole = infoEcoleService.getInfoEcoleById(ecoleId);
        if (!ecole.isPresent()) {
            return false;
        }

        boolean hasAccess = ecole.get().getUser().getId().equals(user.getId());

        if (!hasAccess) {
            logger.warn("SÉCURITÉ: Utilisateur {} a tenté d'accéder à l'école {} d'un autre utilisateur",
                    user.getEmail(), ecoleId);
        }

        return hasAccess;
    }

    /**
     * Obtenir l'IP de l'utilisateur actuel (pour les logs)
     */
    private String getCurrentUserIP() {
        // Implémentation basique - à améliorer selon le contexte
        return "unknown";
    }

    /**
     * Classe pour encapsuler le résultat des vérifications de sécurité
     */
    public static class SecurityCheck {
        private final User user;
        private final InfoEcole userEcole;
        private final String userEtablissement;
        private final boolean isAuthorized;
        private final boolean hasSchool;

        private SecurityCheck(User user, InfoEcole userEcole, String userEtablissement,
                              boolean isAuthorized, boolean hasSchool) {
            this.user = user;
            this.userEcole = userEcole;
            this.userEtablissement = userEtablissement;
            this.isAuthorized = isAuthorized;
            this.hasSchool = hasSchool;
        }

        public static SecurityCheck unauthorized() {
            return new SecurityCheck(null, null, null, false, false);
        }

        public static SecurityCheck noSchool(User user) {
            return new SecurityCheck(user, null, null, false, false);
        }

        public static SecurityCheck authorized(User user, InfoEcole ecole) {
            return new SecurityCheck(user, ecole, ecole.getEtablissement(), true, true);
        }

        // Getters
        public User getUser() { return user; }
        public InfoEcole getUserEcole() { return userEcole; }
        public String getUserEtablissement() { return userEtablissement; }
        public boolean isAuthorized() { return isAuthorized; }
        public boolean hasSchool() { return hasSchool; }
        public boolean isValid() { return isAuthorized && hasSchool; }
    }
}