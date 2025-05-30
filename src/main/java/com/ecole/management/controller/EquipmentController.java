package com.ecole.management.controller;

import com.ecole.management.model.Equipment;
import com.ecole.management.service.EquipmentService;
import com.ecole.management.service.InfoEcoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/equipements")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final InfoEcoleService infoEcoleService;

    @GetMapping
    public String listEquipments(Model model) {
        try {
            List<Equipment> equipments = equipmentService.getAllEquipments();
            model.addAttribute("equipements", equipments);
            return "equipements/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement des équipements: " + e.getMessage());
            return "equipements/list";
        }
    }

    @GetMapping("/new")
    public String showNewEquipmentForm(Model model) {
        model.addAttribute("equipement", new Equipment());
        model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
        return "equipements/form";
    }

    @GetMapping("/{id}")
    public String showEquipmentDetails(@PathVariable Integer id, Model model) {
        equipmentService.getEquipmentById(id)
                .ifPresent(equipement -> model.addAttribute("equipement", equipement));
        return "equipements/details";
    }

    @GetMapping("/edit/{id}")
    public String showEditEquipmentForm(@PathVariable Integer id, Model model) {
        equipmentService.getEquipmentById(id)
                .ifPresent(equipement -> model.addAttribute("equipement", equipement));
        model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
        return "equipements/form";
    }

    @PostMapping("/save")
    public String saveEquipment(@Valid @ModelAttribute("equipement") Equipment equipment,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            model.addAttribute("errorMessage", "Veuillez corriger les erreurs dans le formulaire");
            return "equipements/form";
        }

        try {
            // Set date if not provided
            if (equipment.getDate() == null) {
                equipment.setDate(new Date());
            }

            // Vérifier que l'établissement existe
            if (!infoEcoleService.getInfoEcoleByEtablissement(equipment.getEtablissement()).isPresent()) {
                model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
                model.addAttribute("errorMessage", "L'établissement sélectionné n'existe pas");
                return "equipements/form";
            }

            // Calculate sum if not already set
            if (equipment.getNbr() != null && equipment.getPrix_unitaire() != null) {
                equipment.setSomme(equipment.getNbr() * equipment.getPrix_unitaire());
            }

            equipmentService.saveEquipment(equipment);
            redirectAttributes.addFlashAttribute("successMessage",
                    equipment.getCode() != null ? "Équipement modifié avec succès" : "Équipement enregistré avec succès");
            return "redirect:/equipements";

        } catch (Exception e) {
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            model.addAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "equipements/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        equipmentService.deleteEquipment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Équipement supprimé avec succès");
        return "redirect:/equipements";
    }
    
    @GetMapping("/etablissement/{etablissement}")
    public String listEquipmentsByEtablissement(@PathVariable String etablissement, Model model) {
        model.addAttribute("equipements", equipmentService.getEquipmentsByEtablissement(etablissement));
        model.addAttribute("etablissement", etablissement);
        return "equipements/list";
    }
}