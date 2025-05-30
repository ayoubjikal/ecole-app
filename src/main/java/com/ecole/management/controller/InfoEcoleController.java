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

import java.util.Date;

@Controller
@RequestMapping("/ecoles")
@RequiredArgsConstructor
public class InfoEcoleController {

    private final InfoEcoleService infoEcoleService;

    @GetMapping
    public String listEcoles(Model model) {
        model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
        return "ecoles/list";
    }

    @GetMapping("/new")
    public String showNewEcoleForm(Model model) {
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

            infoEcoleService.saveInfoEcole(infoEcole);
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