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

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactionsBySender(User sender) {
        logger.info("Fetching all transactions from sender: " + sender.getUsername());
        return transactionRepository.findBySender(sender);
    }

    public void saveNewTransaction(User sender, User receiver, String description, double amount) {
        Transaction transaction = new Transaction(sender, receiver, description, amount);
        logger.info("Saving transaction: " + transaction.getDescription());
        transactionRepository.save(transaction);
    }
}
