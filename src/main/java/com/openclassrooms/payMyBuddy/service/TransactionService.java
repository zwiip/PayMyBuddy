package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public boolean isTransactionValid(Transaction transaction) {
        logger.info("Checking if transaction valid: " + transaction.getSender().getUsername());
        if (transaction.getSender().getWallet() <= 0) {
            logger.warning("Insufficient balance for user: " + transaction.getSender().getUsername());
            return false;
        }
        if (transaction.getSender().getWallet() < transaction.getAmount()) {
            logger.warning("Invalid transaction amount: " + transaction.getAmount());
            return false;
        }

        transaction.getSender().setWallet(transaction.getSender().getWallet() - transaction.getAmount());
        transaction.getReceiver().setWallet(transaction.getReceiver().getWallet() + transaction.getAmount());
        logger.info("Transaction valid with the amount: " +transaction.getAmount());

        userService.saveUser(transaction.getSender());
        logger.info(transaction.getSender().getUsername() + "'s new balance: " + transaction.getSender().getWallet());
        userService.saveUser(transaction.getReceiver());
        logger.info(transaction.getReceiver().getUsername() + "'s new balance: " + transaction.getReceiver().getWallet());

        return true;
    }

    public void saveNewTransaction(User sender, String buddyEmail, String description, double amount) {
        User receiver = userService.findUserByEmail(buddyEmail);
        Transaction transaction = new Transaction(sender, receiver, description, amount);
        if(isTransactionValid(transaction)) {
            logger.info("Saving transaction: " + transaction.getDescription());
            transactionRepository.save(transaction);
        } else {
            logger.warning("Transaction could not be saved");
        }
    }
}
