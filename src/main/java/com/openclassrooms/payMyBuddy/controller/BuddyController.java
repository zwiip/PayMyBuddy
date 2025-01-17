package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BuddyController {
    private final UserService userService;

    public BuddyController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/addBuddy")
    public String addBuddy(Model model) {
        model.addAttribute("activePage","addBuddy");
        return "/addBuddy";
    }

    @PostMapping("/addBuddy")
    public String addBuddy(@RequestParam("buddyEmail") String buddyEmail, Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            userService.addNewBuddy(buddyEmail, currentUser);
            model.addAttribute("successMessage", "Buddy ajouté avec succès !");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de l'ajout du Buddy : " + e.getMessage());
        }

        return "/addBuddy";
    }
}
