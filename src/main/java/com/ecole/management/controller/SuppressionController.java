package com.ecole.management.controller;

import com.ecole.management.model.Suppression;
import com.ecole.management.service.InfoEcoleService;
import com.ecole.management.service.SuppressionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
@RequestMapping("/suppressions")
@RequiredArgsConstructor
public class SuppressionController {

    private final SuppressionService suppressionService;
    private final InfoEcoleService infoEcoleService;

    @GetMapping
    public String listSuppressions(Model model) {
        model.addAttribute("suppressions", suppressionService.getAllSuppressions());
        return "suppressions/list";
    }

    @GetMapping("/new")
    public String showNewSuppressionForm(Model model) {
        model.addAttribute("suppression", new Suppression());
        model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
        return "suppressions/form";
    }

    @GetMapping("/{id}")
    public String showSuppressionDetails(@PathVariable Integer id, Model model) {
        suppressionService.getSuppressionById(id)
                .ifPresent(suppression -> model.addAttribute("suppression", suppression));
        return "suppressions/details";
    }

    @GetMapping("/edit/{id}")
    public String showEditSuppressionForm(@PathVariable Integer id, Model model) {
        suppressionService.getSuppressionById(id)
                .ifPresent(suppression -> model.addAttribute("suppression", suppression));
        model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
        return "suppressions/form";
    }

    @PostMapping("/save")
    public String saveSuppression(@Valid @ModelAttribute Suppression suppression,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("ecoles", infoEcoleService.getAllInfoEcoles());
            return "suppressions/form";
        }
        
        // Set date if not provided
        if (suppression.getDate() == null) {
            suppression.setDate(new Date());
        }
        
        suppressionService.saveSuppression(suppression);
        redirectAttributes.addFlashAttribute("successMessage", "Suppression enregistrée avec succès");
        return "redirect:/suppressions";
    }

    @GetMapping("/delete/{id}")
    public String deleteSuppression(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        suppressionService.deleteSuppression(id);
        redirectAttributes.addFlashAttribute("successMessage", "Suppression supprimée avec succès");
        return "redirect:/suppressions";
    }
    
    @GetMapping("/etablissement/{etablissement}")
    public String listSuppressionsByEtablissement(@PathVariable String etablissement, Model model) {
        model.addAttribute("suppressions", suppressionService.getSuppressionsByEtablissement(etablissement));
        model.addAttribute("etablissement", etablissement);
        return "suppressions/list";
    }
}