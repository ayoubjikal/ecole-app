package com.ecole.management.controller;

import com.ecole.management.model.InfoEcole;
import com.ecole.management.service.InfoEcoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ecole.management.service.UserService;
import com.ecole.management.model.User;
import java.util.List;
import java.util.Date;

@Controller
@RequestMapping("/ecoles")
@RequiredArgsConstructor
public class InfoEcoleController {

    private final InfoEcoleService infoEcoleService;
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

    @GetMapping("/{etablissement}")
    public String showEcoleDetails(@PathVariable String etablissement, Model model) {
        infoEcoleService.getInfoEcoleByEtablissement(etablissement)
                .ifPresent(ecole -> model.addAttribute("ecole", ecole));
        return "ecoles/details";
    }

    @GetMapping("/edit/{etablissement}")
    public String showEditEcoleForm(@PathVariable String etablissement, Model model) {
        infoEcoleService.getInfoEcoleByEtablissement(etablissement)
                .ifPresent(ecole -> model.addAttribute("ecole", ecole));
        return "ecoles/form";
    }

    @PostMapping("/save")
    public String saveEcole(@Valid @ModelAttribute("ecole") InfoEcole infoEcole,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Veuillez corriger les erreurs dans le formulaire");
            return "ecoles/form";
        }

        try {
            // Ensure date is set if not provided
            if (infoEcole.getDateDeFondationOuRenouvellement() == null) {
                infoEcole.setDateDeFondationOuRenouvellement(new Date());
            }

            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vous devez être connecté pour créer une école");
                return "redirect:/login";
            }

            infoEcoleService.saveInfoEcoleForUser(infoEcole, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "École enregistrée avec succès");
            return "redirect:/ecoles";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "ecoles/form";
        }
    }

    @GetMapping("/delete/{etablissement}")
    public String deleteEcole(@PathVariable String etablissement, RedirectAttributes redirectAttributes) {
        infoEcoleService.deleteInfoEcole(etablissement);
        redirectAttributes.addFlashAttribute("successMessage", "École supprimée avec succès");
        return "redirect:/ecoles";
    }
}