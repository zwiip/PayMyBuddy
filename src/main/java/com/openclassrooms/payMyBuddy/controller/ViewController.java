package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;
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
    public String signin(@ModelAttribute("user") User user) {
        userService.saveUser(user);
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
        transactionService.saveNewTransaction(sender, buddyEmail, description, amount);
        return "/transfer";
    }

    @GetMapping("/addBuddy")
    public String addBuddy(Model model) {
        return "/addBuddy";
    }

    @PostMapping("/addBuddy")
    public String addBuddy(@RequestParam("buddyEmail") String buddyEmail, Model model) {
        User currentUser = userService.getCurrentUser();
        userService.addNewBuddy(buddyEmail, currentUser);
        return "/addBuddy";
    }

}
