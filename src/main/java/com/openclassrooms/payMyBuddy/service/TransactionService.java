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

    public boolean isTransactionValid(Transaction transaction) {
        logger.fine("Checking if transaction valid: " + transaction.getSender().getUsername());
        double walletBalance = transaction.getSender().getWallet();

        if (walletBalance <= 0) {
            logger.warning("Insufficient balance for user: " + transaction.getSender().getUsername());
            return false;
        }
        if (walletBalance < transaction.getAmount()) {
            logger.warning("Invalid transaction amount: " + transaction.getAmount());
            return false;
        }

        logger.info("Transaction valid with the amount: " + transaction.getAmount());
        return true;
    }

    @Transactional
    public void saveNewTransaction(User sender, String buddyEmail, String description, double amount) {
        User receiver = userService.findUserByEmail(buddyEmail);

        Transaction transaction = new Transaction(sender, receiver, description, amount);

        if(isTransactionValid(transaction)) {
            userService.updateWallet(sender, sender.getWallet() - transaction.getAmount());
            logger.info(sender.getUsername() + "'s new balance: " + sender.getWallet());

            userService.updateWallet(receiver, receiver.getWallet() + transaction.getAmount());
            logger.info(receiver.getUsername() + "'s new balance: " + receiver.getWallet());

            logger.info("Saving transaction: " + transaction.getDescription());
            transactionRepository.save(transaction);
        } else {
            logger.warning("Transaction could not be saved");
            throw new RuntimeException("Transaction could not be saved for the amount: " + amount + " and a wallet of " + sender.getWallet());
        }
    }
}
