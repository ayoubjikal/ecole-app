package com.ecole.management.controller;

import com.ecole.management.model.*;
import com.ecole.management.service.EquipmentService;
import com.ecole.management.service.CategoryService;
import com.ecole.management.service.InfoEcoleService;
import com.ecole.management.service.SuppressionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.ecole.management.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/equipements")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final CategoryService categoryService;
    private final InfoEcoleService infoEcoleService;
    private final SuppressionService suppressionService;
    private final UserService userService;


    @GetMapping
    public String listEquipments(Model model,
                                 @RequestParam(required = false) Integer categoryId,
                                 @RequestParam(required = false) String etablissement,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "25") int size) {
        try {
            // SÉCURITÉ : Vérifier que l'utilisateur est connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }

            // SÉCURITÉ : Vérifier que l'utilisateur a une école
            Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(currentUser);
            if (!userEcole.isPresent()) {
                model.addAttribute("errorMessage", "Vous devez d'abord créer une école pour gérer des équipements.");
                model.addAttribute("equipements", List.of());
                Page<Equipment> emptyPage = Page.empty(PageRequest.of(0, size));
                model.addAttribute("equipmentPage", emptyPage);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("etablissements", List.of());
                return "equipements/list";
            }

            String userEtablissement = userEcole.get().getEtablissement();

            // Clean up empty string parameters to null
            if (etablissement != null && etablissement.trim().isEmpty()) {
                etablissement = null;
            }
            if (status != null && status.trim().isEmpty()) {
                status = null;
            }

            // SÉCURITÉ : Forcer l'établissement de l'utilisateur connecté
            if (etablissement != null && !etablissement.equals(userEtablissement)) {
                // Si l'utilisateur essaie d'accéder aux données d'un autre établissement, rediriger
                return "redirect:/equipements";
            }
            etablissement = userEtablissement; // Toujours utiliser l'établissement de l'utilisateur

            // Ensure page is not negative
            page = Math.max(0, page);
            size = Math.max(1, Math.min(100, size));

            // Create Pageable object for pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by("equipmentId").ascending());

            Page<Equipment> equipmentPage;

            // Parse status parameter
            EquipmentStatus equipmentStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    equipmentStatus = EquipmentStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    equipmentStatus = null;
                }
            }

            // Filter logic TOUJOURS limité à l'établissement de l'utilisateur
            if (categoryId != null && equipmentStatus != null) {
                Category category = categoryService.getCategoryById(categoryId).orElse(null);
                if (category != null) {
                    equipmentPage = equipmentService.getEquipmentsByStatusAndCategoryAndEtablissementPaginated(equipmentStatus, category, etablissement, pageable);
                } else {
                    equipmentPage = equipmentService.getEquipmentsByStatusAndEtablissementPaginated(equipmentStatus, etablissement, pageable);
                }
            } else if (categoryId != null) {
                Category category = categoryService.getCategoryById(categoryId).orElse(null);
                if (category != null) {
                    equipmentPage = equipmentService.getEquipmentsByCategoryAndEtablissementPaginated(category, etablissement, pageable);
                } else {
                    equipmentPage = equipmentService.getEquipmentsByEtablissementPaginated(etablissement, pageable);
                }
            } else if (equipmentStatus != null) {
                equipmentPage = equipmentService.getEquipmentsByStatusAndEtablissementPaginated(equipmentStatus, etablissement, pageable);
            } else {
                // CAS PAR DÉFAUT : Afficher tous les équipements de l'établissement de l'utilisateur
                equipmentPage = equipmentService.getEquipmentsByEtablissementPaginated(etablissement, pageable);
            }

            // Make sure we have valid equipmentPage
            if (equipmentPage == null) {
                equipmentPage = equipmentService.getEquipmentsByEtablissementPaginated(etablissement, pageable);
            }

            model.addAttribute("equipements", equipmentPage.getContent());
            model.addAttribute("equipmentPage", equipmentPage);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("etablissements", infoEcoleService.getEcolesForCurrentUser(currentUser));
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedEtablissement", etablissement);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

            return "equipements/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Erreur lors du chargement des équipements: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/supprimes")
    public String listSupprimedEquipments(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(currentUser);
        if (userEcole.isPresent()) {
            String etablissement = userEcole.get().getEtablissement();
            List<Equipment> supprimedEquipments = equipmentService.getEquipmentsByEtablissement(etablissement)
                    .stream()
                    .filter(eq -> eq.getStatus() == EquipmentStatus.SUPPRIME)
                    .collect(Collectors.toList());
            model.addAttribute("equipements", supprimedEquipments);
        } else {
            model.addAttribute("equipements", List.of());
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("etablissements", infoEcoleService.getEcolesForCurrentUser(currentUser));
        model.addAttribute("selectedStatus", "SUPPRIME");
        return "equipements/list";
    }

    @GetMapping("/new")
    public String showNewEquipmentForm(Model model) {
        model.addAttribute("equipementForm", new EquipmentFormDTO());
        model.addAttribute("categories", categoryService.getAllCategories());

        // Établissements dynamiques pour l'utilisateur connecté
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
        } else {
            model.addAttribute("ecoles", List.of());
        }

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

            // Établissements dynamiques pour l'utilisateur connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
            } else {
                model.addAttribute("ecoles", List.of());
            }

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

            // Établissements dynamiques
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
            } else {
                model.addAttribute("ecoles", List.of());
            }

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
// Établissements dynamiques pour l'utilisateur connecté
                User currentUser = userService.getCurrentUser();
                if (currentUser != null) {
                    model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
                } else {
                    model.addAttribute("ecoles", List.of());
                }                model.addAttribute("errorMessage", "L'établissement sélectionné n'existe pas");
                return "equipements/form";
            }

            // Vérifier que la catégorie existe
            Category category = categoryService.getCategoryById(equipmentForm.getCategoryId()).orElse(null);
            if (category == null) {
                model.addAttribute("categories", categoryService.getAllCategories());
// Établissements dynamiques pour l'utilisateur connecté
                User currentUser = userService.getCurrentUser();
                if (currentUser != null) {
                    model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
                } else {
                    model.addAttribute("ecoles", List.of());
                }                model.addAttribute("errorMessage", "La catégorie sélectionnée n'existe pas");
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

            // Établissements dynamiques
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
            } else {
                model.addAttribute("ecoles", List.of());
            }

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
// Établissements dynamiques pour l'utilisateur connecté
                User currentUser = userService.getCurrentUser();
                if (currentUser != null) {
                    model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
                } else {
                    model.addAttribute("ecoles", List.of());
                }                model.addAttribute("errorMessage", "Catégorie non trouvée");
                return "equipements/edit-form";
            }

            // Vérifier que l'établissement existe
            if (!infoEcoleService.getInfoEcoleByEtablissement(etablissement).isPresent()) {
                model.addAttribute("equipement", equipment);
                model.addAttribute("categories", categoryService.getAllCategories());
// Établissements dynamiques pour l'utilisateur connecté
                User currentUser = userService.getCurrentUser();
                if (currentUser != null) {
                    model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
                } else {
                    model.addAttribute("ecoles", List.of());
                }                model.addAttribute("errorMessage", "L'établissement sélectionné n'existe pas");
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

            // Établissements dynamiques
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("ecoles", infoEcoleService.getEcolesForCurrentUser(currentUser));
            } else {
                model.addAttribute("ecoles", List.of());
            }

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
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(currentUser);
        if (!userEcole.isPresent()) {
            return "redirect:/ecoles/new";
        }

        String etablissement = userEcole.get().getEtablissement();
        Category category = categoryService.getCategoryById(categoryId).orElse(null);

        if (category != null) {
            model.addAttribute("equipements", equipmentService.getEquipmentsByCategoryAndEtablissement(category, etablissement));
            model.addAttribute("selectedCategory", category);
        } else {
            model.addAttribute("equipements", equipmentService.getEquipmentsByEtablissement(etablissement));
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("etablissements", infoEcoleService.getEcolesForCurrentUser(currentUser));
        return "equipements/list";
    }

    @GetMapping("/print/list")
    public String showPrintListView(Model model,
                                    @RequestParam(required = false) Integer categoryId,
                                    @RequestParam(required = false) String etablissement,
                                    @RequestParam(required = false) String status) {
        try {
            // SÉCURITÉ: Vérifier que l'utilisateur est connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }

            // SÉCURITÉ: Vérifier que l'utilisateur a une école
            Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(currentUser);
            if (!userEcole.isPresent()) {
                model.addAttribute("errorMessage", "Vous devez d'abord créer une école.");
                return "redirect:/equipements";
            }

            String userEtablissement = userEcole.get().getEtablissement();

            // SÉCURITÉ: Forcer l'établissement de l'utilisateur connecté
            etablissement = userEtablissement;

            List<Equipment> equipments;

            // Parse status parameter
            EquipmentStatus equipmentStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    equipmentStatus = EquipmentStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    equipmentStatus = null;
                }
            }

            // Get equipments filtered by user's etablissement
            if (categoryId != null && equipmentStatus != null) {
                Category category = categoryService.getCategoryById(categoryId).orElse(null);
                if (category != null) {
                    equipments = equipmentService.getEquipmentsByStatusAndCategoryAndEtablissement(equipmentStatus, category, etablissement);
                } else {
                    equipments = equipmentService.getEquipmentsByStatusAndEtablissement(equipmentStatus, etablissement);
                }
            } else if (equipmentStatus != null) {
                equipments = equipmentService.getEquipmentsByStatusAndEtablissement(equipmentStatus, etablissement);
            } else if (categoryId != null) {
                Category category = categoryService.getCategoryById(categoryId).orElse(null);
                if (category != null) {
                    equipments = equipmentService.getEquipmentsByCategoryAndEtablissement(category, etablissement);
                } else {
                    equipments = equipmentService.getEquipmentsByEtablissement(etablissement);
                }
            } else {
                equipments = equipmentService.getEquipmentsByEtablissement(etablissement);
            }

            model.addAttribute("equipements", equipments);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedEtablissement", etablissement);
            model.addAttribute("selectedStatus", status);

            return "equipements/print";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la génération de l'impression: " + e.getMessage());
            return "redirect:/equipements";
        }
    }

    @GetMapping("/print/combined")
    public String showCombinedPrintView(Model model,
                                        @RequestParam(required = false) Integer categoryId,
                                        @RequestParam(required = false) String etablissement) {
        try {
            // SÉCURITÉ: Vérifier que l'utilisateur est connecté
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/login";
            }

            // SÉCURITÉ: Vérifier que l'utilisateur a une école
            Optional<InfoEcole> userEcole = infoEcoleService.getInfoEcoleByUser(currentUser);
            if (!userEcole.isPresent()) {
                model.addAttribute("errorMessage", "Vous devez d'abord créer une école.");
                return "redirect:/equipements";
            }

            String userEtablissement = userEcole.get().getEtablissement();

            // SÉCURITÉ: Forcer l'établissement de l'utilisateur connecté
            etablissement = userEtablissement;

            // Get equipment data filtered by user's etablissement
            List<Equipment> activeEquipments;
            List<Suppression> suppressions;

            if (categoryId != null) {
                Category category = categoryService.getCategoryById(categoryId).orElse(null);
                if (category != null) {
                    activeEquipments = equipmentService.getActiveEquipmentsByCategoryAndEtablissement(category, etablissement);
                    String finalEtablissement = etablissement;
                    suppressions = suppressionService.getSuppressionsByCategory(categoryId)
                            .stream().filter(s -> finalEtablissement.equals(s.getEtablissement())).toList();
                    model.addAttribute("category", category);
                } else {
                    activeEquipments = equipmentService.getActiveEquipmentsByEtablissement(etablissement);
                    suppressions = suppressionService.getSuppressionsByEtablissement(etablissement);
                }
            } else {
                activeEquipments = equipmentService.getActiveEquipmentsByEtablissement(etablissement);
                suppressions = suppressionService.getSuppressionsByEtablissement(etablissement);
            }

            model.addAttribute("etablissement", etablissement);

            // Calculate statistics
            Map<String, Long> equipmentStats = new HashMap<>();
            equipmentStats.put("total", (long) (activeEquipments.size() + suppressions.size()));
            equipmentStats.put("active", (long) activeEquipments.size());
            equipmentStats.put("supprime", (long) suppressions.size());

            // Calculate total value
            double totalValue = activeEquipments.stream().mapToDouble(e -> e.getPrix_unitaire() != null ? e.getPrix_unitaire() : 0).sum() +
                    suppressions.stream().mapToDouble(s -> s.getPrixUnitaire() != null ? s.getPrixUnitaire() : 0).sum();

            // Category summary - ONLY for user's equipment
            Map<Integer, Map<String, Object>> categorySummary = new HashMap<>();
            List<Category> allCategories = categoryService.getAllCategories();

            for (Category cat : allCategories) {
                Map<String, Object> catStats = new HashMap<>();

                long activeCount = activeEquipments.stream().filter(e -> e.getCategory().getId().equals(cat.getId())).count();
                long suppressionCount = suppressions.stream().filter(s -> s.getEquipment().getCategory().getId().equals(cat.getId())).count();
                double catValue = activeEquipments.stream()
                        .filter(e -> e.getCategory().getId().equals(cat.getId()))
                        .mapToDouble(e -> e.getPrix_unitaire() != null ? e.getPrix_unitaire() : 0).sum() +
                        suppressions.stream()
                                .filter(s -> s.getEquipment().getCategory().getId().equals(cat.getId()))
                                .mapToDouble(s -> s.getPrixUnitaire() != null ? s.getPrixUnitaire() : 0).sum();

                catStats.put("total", activeCount + suppressionCount);
                catStats.put("active", activeCount);
                catStats.put("supprime", suppressionCount);
                catStats.put("value", catValue);

                categorySummary.put(cat.getId(), catStats);
            }

            model.addAttribute("activeEquipments", activeEquipments);
            model.addAttribute("suppressions", suppressions);
            model.addAttribute("equipmentStats", equipmentStats);
            model.addAttribute("totalValue", totalValue);
            model.addAttribute("categories", allCategories);
            model.addAttribute("categorySummary", categorySummary);

            return "reports/combined-print";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la génération du rapport: " + e.getMessage());
            return "redirect:/equipements";
        }
    }

    // Helper method to generate page numbers for pagination
    private java.util.List<Integer> generatePageNumbers(int startPage, int endPage) {
        java.util.List<Integer> pageNumbers = new java.util.ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }


}