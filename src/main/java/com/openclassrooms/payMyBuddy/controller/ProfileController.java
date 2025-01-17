package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("activePage","profile");

        return "/profile";
    }

    @PostMapping("/profile")
    public String profile(@ModelAttribute("user") User updatedUser, Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            userService.updateUser(updatedUser, currentUser);

            model.addAttribute("successMessage", "Profil mis à jour avec succès !");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la mise à jour du profil : " + e.getMessage());
        }

        return "/profile";
    }
}
