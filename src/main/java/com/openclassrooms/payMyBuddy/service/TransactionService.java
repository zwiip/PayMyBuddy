package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactionsBySender(User sender) {
        return transactionRepository.findBySender(sender);
    }

    public void saveNewTransaction(User sender, User receiver, String description, double amount) {
        Transaction transaction = new Transaction(sender, receiver, description, amount);
        transactionRepository.save(transaction);
    }
}
