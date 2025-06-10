package com.ecole.management.controller;

import com.ecole.management.model.Suppression;
import com.ecole.management.model.SuppressionFormDTO;
import com.ecole.management.model.Equipment;
import com.ecole.management.service.SuppressionService;
import com.ecole.management.service.EquipmentService;
import com.ecole.management.service.CategoryService;
import com.ecole.management.service.InfoEcoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/suppressions")
@RequiredArgsConstructor
public class SuppressionController {

    private final SuppressionService suppressionService;
    private final EquipmentService equipmentService;
    private final CategoryService categoryService;
    private final InfoEcoleService infoEcoleService;

    @GetMapping
    public String listSuppressions(Model model,
                                   @RequestParam(required = false) Integer categoryId,
                                   @RequestParam(required = false) String etablissement) {
        try {
            List<Suppression> suppressions;

            if (categoryId != null && etablissement != null) {
                // Filter by both category and etablissement
                suppressions = suppressionService.getSuppressionsByCategory(categoryId)
                        .stream()
                        .filter(s -> etablissement.equals(s.getEtablissement()))
                        .toList();
            } else if (categoryId != null) {
                suppressions = suppressionService.getSuppressionsByCategory(categoryId);
            } else if (etablissement != null) {
                suppressions = suppressionService.getSuppressionsByEtablissement(etablissement);
            } else {
                suppressions = suppressionService.getAllSuppressions();
            }

            model.addAttribute("suppressions", suppressions);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedEtablissement", etablissement);

            return "suppressions/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement des suppressions: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            return "suppressions/list";
        }
    }

    @GetMapping("/new")
    public String showNewSuppressionForm(Model model,
                                         @RequestParam(required = false) String etablissement) {
        model.addAttribute("suppressionForm", new SuppressionFormDTO());

        // Get available equipment for suppression
        List<Equipment> availableEquipments;
        if (etablissement != null && !etablissement.isEmpty()) {
            availableEquipments = suppressionService.getAvailableEquipmentsForSuppressionByEtablissement(etablissement);
            model.addAttribute("selectedEtablissement", etablissement);
        } else {
            availableEquipments = suppressionService.getAvailableEquipmentsForSuppression();
        }

        model.addAttribute("availableEquipments", availableEquipments);
        model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "suppressions/form";
    }

    @GetMapping("/{id}")
    public String showSuppressionDetails(@PathVariable Integer id, Model model) {
        suppressionService.getSuppressionById(id)
                .ifPresent(suppression -> model.addAttribute("suppression", suppression));
        return "suppressions/details";
    }

    @PostMapping("/save")
    public String saveSuppression(@Valid @ModelAttribute("suppressionForm") SuppressionFormDTO suppressionForm,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        if (result.hasErrors()) {
            model.addAttribute("availableEquipments", suppressionService.getAvailableEquipmentsForSuppression());
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("errorMessage", "Veuillez corriger les erreurs dans le formulaire");
            return "suppressions/form";
        }

        try {
            // Set date if not provided
            if (suppressionForm.getDateSuppression() == null) {
                suppressionForm.setDateSuppression(new Date());
            }

            Suppression suppression = suppressionService.createSuppressionFromEquipment(suppressionForm);

            String message = String.format("Suppression créée avec succès pour l'équipement %s",
                    suppression.getEquipmentId());
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/suppressions";

        } catch (Exception e) {
            model.addAttribute("availableEquipments", suppressionService.getAvailableEquipmentsForSuppression());
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "suppressions/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteSuppression(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Suppression suppression = suppressionService.getSuppressionById(id).orElse(null);
            String equipmentInfo = "suppression";
            if (suppression != null) {
                equipmentInfo = "suppression de l'équipement " + suppression.getEquipmentId();
            }

            suppressionService.deleteSuppression(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ " + equipmentInfo + " annulée avec succès. L'équipement a été réactivé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/suppressions";
    }

    @GetMapping("/etablissement/{etablissement}")
    public String listSuppressionsByEtablissement(@PathVariable String etablissement, Model model) {
        model.addAttribute("suppressions", suppressionService.getSuppressionsByEtablissement(etablissement));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
        model.addAttribute("etablissement", etablissement);
        return "suppressions/list";
    }

    @GetMapping("/category/{categoryId}")
    public String listSuppressionsByCategory(@PathVariable Integer categoryId, Model model) {
        model.addAttribute("suppressions", suppressionService.getSuppressionsByCategory(categoryId));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
        model.addAttribute("selectedCategoryId", categoryId);
        return "suppressions/list";
    }

    // REST endpoint to get equipment details by ID (for AJAX)
    @GetMapping("/api/equipment/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEquipmentDetails(@PathVariable Integer id) {
        try {
            Equipment equipment = equipmentService.getEquipmentById(id).orElse(null);
            if (equipment == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("equipmentId", equipment.getEquipmentId());
            response.put("designation", equipment.getDesignation());
            response.put("category", equipment.getCategory().getName());
            response.put("categorySymbol", equipment.getCategory().getSymbol());
            response.put("etablissement", equipment.getEtablissement());
            response.put("prixUnitaire", equipment.getPrix_unitaire());
            response.put("source", equipment.getSource_equipment());
            response.put("specialisation", equipment.getSpecialisation());
            response.put("etat", equipment.getEtat());
            response.put("date", equipment.getDate());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}