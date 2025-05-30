package com.ecole.management.controller;


import com.ecole.management.model.Equipment;
import com.ecole.management.model.EquipmentFormDTO;
import com.ecole.management.model.Category;
import com.ecole.management.service.EquipmentService;
import com.ecole.management.service.CategoryService;
import com.ecole.management.service.InfoEcoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/equipements")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final CategoryService categoryService;
    private final InfoEcoleService infoEcoleService;

    @GetMapping
    public String listEquipments(Model model,
                                 @RequestParam(required = false) Integer categoryId,
                                 @RequestParam(required = false) String etablissement) {
        try {
            List<Equipment> equipments;

            if (categoryId != null && etablissement != null) {
                Category category = categoryService.getCategoryById(categoryId).orElse(null);
                if (category != null) {
                    equipments = equipmentService.getEquipmentsByCategoryAndEtablissement(category, etablissement);
                } else {
                    equipments = equipmentService.getEquipmentsByEtablissement(etablissement);
                }
            } else if (categoryId != null) {
                Category category = categoryService.getCategoryById(categoryId).orElse(null);
                if (category != null) {
                    equipments = equipmentService.getEquipmentsByCategory(category);
                } else {
                    equipments = equipmentService.getAllEquipments();
                }
            } else if (etablissement != null) {
                equipments = equipmentService.getEquipmentsByEtablissement(etablissement);
            } else {
                equipments = equipmentService.getAllEquipments();
            }

            model.addAttribute("equipements", equipments);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedEtablissement", etablissement);
            return "equipements/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement des équipements: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "equipements/list";
        }
    }

    @GetMapping("/new")
    public String showNewEquipmentForm(Model model) {
        model.addAttribute("equipementForm", new EquipmentFormDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
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
        Equipment equipment = equipmentService.getEquipmentById(id).orElse(null);
        if (equipment != null) {
            model.addAttribute("equipement", equipment);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            return "equipements/edit-form";
        } else {
            return "redirect:/equipements";
        }
    }

    @PostMapping("/save")
    public String saveEquipment(@Valid @ModelAttribute("equipementForm") EquipmentFormDTO equipmentForm,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            model.addAttribute("errorMessage", "Veuillez corriger les erreurs dans le formulaire");
            return "equipements/form";
        }

        try {
            // Set date if not provided
            if (equipmentForm.getDate() == null) {
                equipmentForm.setDate(new Date());
            }

            // Vérifier que l'établissement existe
            if (!infoEcoleService.getInfoEcoleByEtablissement(equipmentForm.getEtablissement()).isPresent()) {
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
                model.addAttribute("errorMessage", "L'établissement sélectionné n'existe pas");
                return "equipements/form";
            }

            // Vérifier que la catégorie existe
            Category category = categoryService.getCategoryById(equipmentForm.getCategoryId()).orElse(null);
            if (category == null) {
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
                model.addAttribute("errorMessage", "La catégorie sélectionnée n'existe pas");
                return "equipements/form";
            }

            // Create equipment template
            Equipment equipmentTemplate = new Equipment();
            equipmentTemplate.setDate(equipmentForm.getDate());
            equipmentTemplate.setDesignation(equipmentForm.getDesignation());
            equipmentTemplate.setSource_equipment(equipmentForm.getSource_equipment());
            equipmentTemplate.setPrix_unitaire(equipmentForm.getPrix_unitaire());
            equipmentTemplate.setSpecialisation(equipmentForm.getSpecialisation());
            equipmentTemplate.setEtat(equipmentForm.getEtat());
            equipmentTemplate.setRemarque(equipmentForm.getRemarque());
            equipmentTemplate.setEtablissement(equipmentForm.getEtablissement());
            equipmentTemplate.setCategory(category);

            // Create multiple equipments based on quantity
            List<Equipment> createdEquipments = equipmentService.createEquipments(equipmentTemplate, equipmentForm.getQuantity());

            String message = String.format("%d équipement(s) créé(s) avec succès", createdEquipments.size());
            if (createdEquipments.size() > 1) {
                String firstId = createdEquipments.get(0).getEquipmentId();
                String lastId = createdEquipments.get(createdEquipments.size() - 1).getEquipmentId();
                message += String.format(" (IDs: %s à %s)", firstId, lastId);
            } else if (createdEquipments.size() == 1) {
                message += String.format(" (ID: %s)", createdEquipments.get(0).getEquipmentId());
            }

            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/equipements";

        } catch (Exception e) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            model.addAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "equipements/form";
        }
    }

    @PostMapping("/update/{id}")
    public String updateEquipment(@PathVariable Integer id,
                                  @RequestParam String equipmentId,
                                  @RequestParam Integer categoryId,
                                  @RequestParam String date,
                                  @RequestParam String designation,
                                  @RequestParam(required = false) String source_equipment,
                                  @RequestParam String etablissement,
                                  @RequestParam Double prix_unitaire,
                                  @RequestParam(required = false) String specialisation,
                                  @RequestParam(required = false) String etat,
                                  @RequestParam(required = false) String remarque,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        try {
            // Get the existing equipment
            Equipment equipment = equipmentService.getEquipmentById(id).orElse(null);
            if (equipment == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Équipement non trouvé");
                return "redirect:/equipements";
            }

            // Get the category
            Category category = categoryService.getCategoryById(categoryId).orElse(null);
            if (category == null) {
                model.addAttribute("equipement", equipment);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
                model.addAttribute("errorMessage", "Catégorie non trouvée");
                return "equipements/edit-form";
            }

            // Vérifier que l'établissement existe
            if (!infoEcoleService.getInfoEcoleByEtablissement(etablissement).isPresent()) {
                model.addAttribute("equipement", equipment);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
                model.addAttribute("errorMessage", "L'établissement sélectionné n'existe pas");
                return "equipements/edit-form";
            }

            // Parse date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);

            // Update equipment fields
            equipment.setDate(parsedDate);
            equipment.setDesignation(designation);
            equipment.setSource_equipment(source_equipment);
            equipment.setEtablissement(etablissement);
            equipment.setPrix_unitaire(prix_unitaire);
            equipment.setSpecialisation(specialisation);
            equipment.setEtat(etat);
            equipment.setRemarque(remarque);
            equipment.setCategory(category);

            // Calculate sum
            equipment.setSomme(prix_unitaire);

            equipmentService.updateEquipment(equipment);
            redirectAttributes.addFlashAttribute("successMessage", "Équipement modifié avec succès");
            return "redirect:/equipements";

        } catch (Exception e) {
            Equipment equipment = equipmentService.getEquipmentById(id).orElse(new Equipment());
            model.addAttribute("equipement", equipment);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            model.addAttribute("errorMessage", "Erreur lors de la modification : " + e.getMessage());
            return "equipements/edit-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            // Get equipment details for confirmation message
            Optional<Equipment> equipmentOpt = equipmentService.getEquipmentById(id);
            String equipmentInfo = "équipement";
            if (equipmentOpt.isPresent()) {
                Equipment equipment = equipmentOpt.get();
                equipmentInfo = "équipement " + equipment.getEquipmentId() + " (" + equipment.getDesignation() + ")";
            }

            equipmentService.deleteEquipment(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ " + equipmentInfo + " supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/equipements";
    }

    @GetMapping("/etablissement/{etablissement}")
    public String listEquipmentsByEtablissement(@PathVariable String etablissement, Model model) {
        model.addAttribute("equipements", equipmentService.getEquipmentsByEtablissement(etablissement));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("etablissement", etablissement);
        return "equipements/list";
    }

    @GetMapping("/category/{categoryId}")
    public String listEquipmentsByCategory(@PathVariable Integer categoryId, Model model) {
        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        if (category != null) {
            model.addAttribute("equipements", equipmentService.getEquipmentsByCategory(category));
            model.addAttribute("selectedCategory", category);
        } else {
            model.addAttribute("equipements", equipmentService.getAllEquipments());
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        return "equipements/list";
    }
}