package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class TransactionService {

    private final Logger logger = Logger.getLogger(TransactionService.class.getName());

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public List<Transaction> getAllTransactionsBySender(User sender) {
        logger.info("Fetching all transactions from sender: " + sender.getUsername());
        return transactionRepository.findBySender(sender);
    }

    public void saveNewTransaction(User sender, String buddyEmail, String description, double amount) {
        User receiver = userService.findUserByEmail(buddyEmail);
        Transaction transaction = new Transaction(sender, receiver, description, amount);
        logger.info("Saving transaction: " + transaction.getDescription());
        transactionRepository.save(transaction);
    }
}
