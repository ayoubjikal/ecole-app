package com.ecole.management.controller;

import com.ecole.management.model.InfoEcole;
import com.ecole.management.model.User;
import com.ecole.management.service.InfoEcoleService;
import com.ecole.management.service.SecurityService;
import com.ecole.management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ecoles")
@RequiredArgsConstructor
public class InfoEcoleController {

    private final InfoEcoleService infoEcoleService;
    private final SecurityService securityService;
    private final UserService userService;


    @GetMapping
    public String listEcoles(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
            model.addAttribute("userHasEcole", infoEcoleService.userHasEcole(currentUser));
        } else {
            model.addAttribute("ecoles", List.of());
            model.addAttribute("userHasEcole", false);
        }
        return "ecoles/list";
    }


    @GetMapping("/new")
    public String showNewEcoleForm(Model model, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vous devez être connecté pour créer une école");
            return "redirect:/login";
        }

        if (infoEcoleService.userHasEcole(currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vous avez déjà créé une école. Chaque utilisateur ne peut créer qu'une seule école.");
            return "redirect:/ecoles";
        }

        model.addAttribute("ecole", new InfoEcole());
        return "ecoles/form";
    }

    @GetMapping("/{id}")
    public String showEcoleDetails(@PathVariable Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        SecurityService.SecurityCheck securityCheck = securityService.checkUserAccess();

        if (!securityCheck.isValid()) {
            if (!securityCheck.isAuthorized()) {
                return "redirect:/login";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Vous devez d'abord créer une école.");
            return "redirect:/ecoles";
        }

        // CONTRÔLE D'ACCÈS : Vérifier que l'école appartient à l'utilisateur
        if (!securityService.canAccessEcole(id, securityCheck.getUser())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès non autorisé à cette école.");
            return "redirect:/ecoles";
        }

        Optional<InfoEcole> ecole = infoEcoleService.getInfoEcoleById(id);
        if (ecole.isPresent()) {
            model.addAttribute("ecole", ecole.get());
            return "ecoles/details";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "École non trouvée.");
            return "redirect:/ecoles";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditEcoleForm(@PathVariable Long id, Model model,
                                    RedirectAttributes redirectAttributes) {
        SecurityService.SecurityCheck securityCheck = securityService.checkUserAccess();

        if (!securityCheck.isValid()) {
            if (!securityCheck.isAuthorized()) {
                return "redirect:/login";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Vous devez d'abord créer une école.");
            return "redirect:/ecoles";
        }

        // CONTRÔLE D'ACCÈS : Vérifier que l'école appartient à l'utilisateur
        if (!securityService.canAccessEcole(id, securityCheck.getUser())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès non autorisé à cette école.");
            return "redirect:/ecoles";
        }

        Optional<InfoEcole> ecole = infoEcoleService.getInfoEcoleById(id);
        if (ecole.isPresent()) {
            model.addAttribute("ecole", ecole.get());
            return "ecoles/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "École non trouvée.");
            return "redirect:/ecoles";
        }
    }

    @PostMapping("/save")
    public String saveEcole(@Valid @ModelAttribute("ecole") InfoEcole infoEcole,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        SecurityService.SecurityCheck securityCheck = securityService.checkUserAccess();

        if (!securityCheck.isAuthorized()) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Veuillez corriger les erreurs dans le formulaire");
            return "ecoles/form";
        }

        try {
            if (infoEcole.getDateDeFondationOuRenouvellement() == null) {
                infoEcole.setDateDeFondationOuRenouvellement(new Date());
            }

            infoEcoleService.saveInfoEcoleForUser(infoEcole, securityCheck.getUser());
            redirectAttributes.addFlashAttribute("successMessage", "École enregistrée avec succès");
            return "redirect:/ecoles";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "ecoles/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEcole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        SecurityService.SecurityCheck securityCheck = securityService.checkUserAccess();

        if (!securityCheck.isValid()) {
            if (!securityCheck.isAuthorized()) {
                return "redirect:/login";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Vous devez d'abord créer une école.");
            return "redirect:/ecoles";
        }

        try {
            infoEcoleService.deleteInfoEcole(id, securityCheck.getUser());
            redirectAttributes.addFlashAttribute("successMessage", "École supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }

        return "redirect:/ecoles";
    }
}