package com.ecole.management.controller;

import com.ecole.management.model.User;
import com.ecole.management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Email ou mot de passe incorrect.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Vous avez été déconnecté avec succès.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               @RequestParam("confirmPassword") String confirmPassword,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        // Vérifier si le mot de passe correspond à la confirmation
        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.user", "Les mots de passe ne correspondent pas");
        }

        // Vérifier si l'email existe déjà
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Un compte avec cet email existe déjà");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.createUser(user.getEmail(), user.getPassword(),
                    user.getFirstName(), user.getLastName());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Compte créé avec succès! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la création du compte: " + e.getMessage());
            return "auth/register";
        }
    }
}