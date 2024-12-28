package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
public class ViewController {

    private final UserService userService;

    private final TransactionService transactionService;

    public ViewController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }

    @PostMapping("/signin")
    public String signin(@ModelAttribute("user") User user, Model model) {
        try {
            userService.saveUser(user);
            model.addAttribute("successMessage", "Compte utilisateur créé avec succès !");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la création du compte utilisateur : " + e.getMessage());
        }

        return "redirect:/login";
    }

    @GetMapping("/transfer")
    public String transfer(Model model) {
        User currentUser = userService.getCurrentUser();
        Set<User> buddies = userService.getAllMyBuddies(currentUser);
        List<Transaction> transactions = transactionService.getAllTransactionsBySender(currentUser);

        model.addAttribute("buddies", buddies);
        model.addAttribute("noBuddies", buddies.isEmpty());
        model.addAttribute("transactions", transactions);
        model.addAttribute("noTransactions", transactions.isEmpty());

        return "/transfer";
    }

    @PostMapping("/transfer")
    public String addTransaction(@RequestParam("buddyEmail") String buddyEmail, @RequestParam("description") String description, @RequestParam("amount") Double amount, Model model) {
        User sender = userService.getCurrentUser();
        Set<User> buddies = userService.getAllMyBuddies(sender);
        try {
            transactionService.saveNewTransaction(sender, buddyEmail, description, amount);
            model.addAttribute("successMessage", "Transaction effectuée avec succès !");
        } catch ( Exception e ) {
            model.addAttribute("errorMessage", "Erreur lors de la transaction : " + e.getMessage());
        }

        model.addAttribute("transactions", transactionService.getAllTransactionsBySender(sender));
        model.addAttribute("noTransactions", transactionService.getAllTransactionsBySender(sender).isEmpty());
        model.addAttribute("buddies", buddies);
        model.addAttribute("noBuddies", buddies.isEmpty());

        return "/transfer";
    }

    @GetMapping("/addBuddy")
    public String addBuddy() {
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

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);

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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:login";
    }
}
