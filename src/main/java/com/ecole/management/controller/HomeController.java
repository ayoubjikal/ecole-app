package com.ecole.management.controller;

import com.ecole.management.model.User;
import com.ecole.management.service.UserService;
import com.ecole.management.service.InfoEcoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final InfoEcoleService infoEcoleService;

    @GetMapping("/")
    public String home(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userHasEcole", infoEcoleService.userHasEcole(currentUser));
        }
        return "index";
    }
}