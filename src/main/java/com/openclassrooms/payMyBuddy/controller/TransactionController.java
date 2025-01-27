package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
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
        model.addAttribute("activePage","transfer");

        return "transfer";
    }

    @PostMapping("/transfer")
    public String addTransaction(@RequestParam("buddyEmail") String buddyEmail, @RequestParam("description") String description, @RequestParam("amount") Double amount, Model model) {
        User sender = userService.getCurrentUser();
        Set<User> buddies = userService.getAllMyBuddies(sender);
        try {
            transactionService.saveNewTransaction(sender, buddyEmail, description, amount);
            model.addAttribute("successMessage", "Transaction effectuée avec succès !");
        } catch ( Exception e ) {
            model.addAttribute("errorMessage", "Erreur lors de la transaction.");
        }

        model.addAttribute("transactions", transactionService.getAllTransactionsBySender(sender));
        model.addAttribute("noTransactions", transactionService.getAllTransactionsBySender(sender).isEmpty());
        model.addAttribute("buddies", buddies);
        model.addAttribute("noBuddies", buddies.isEmpty());

        return "transfer";
    }
}
