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

@Controller
@RequestMapping("/equipements")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final InfoEcoleService infoEcoleService;

    @GetMapping
    public String listEquipments(Model model) {
        model.addAttribute("equipements", equipmentService.getAllEquipments());
        return "equipements/list";
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
            return "equipements/form";
        }
        
        // Set date if not provided
        if (equipment.getDate() == null) {
            equipment.setDate(new Date());
        }
        
        // Calculate sum if not already set
        if (equipment.getNbr() != null && equipment.getPrix_unitaire() != null) {
            equipment.setSomme(equipment.getNbr() * equipment.getPrix_unitaire());
        }
        
        equipmentService.saveEquipment(equipment);
        redirectAttributes.addFlashAttribute("successMessage", "Équipement enregistré avec succès");
        return "redirect:/equipements";
    }

    @GetMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        equipmentService.deleteEquipment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Équipement supprimé avec succès");
        return "redirect:/equipements";
    }
    
    @GetMapping("/print")
    public String printEquipments(Model model) {
        model.addAttribute("equipements", equipmentService.getAllEquipments());
        return "equipements/print";
    }
    
    @GetMapping("/etablissement/{etablissement}")
    public String listEquipmentsByEtablissement(@PathVariable String etablissement, Model model) {
        model.addAttribute("equipements", equipmentService.getEquipmentsByEtablissement(etablissement));
        model.addAttribute("etablissement", etablissement);
        return "equipements/list";
    }
}