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

    /* VARIABLES */
    private final Logger logger = Logger.getLogger(TransactionService.class.getName());

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    /* CONSTRUCTOR */
    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    /* METHODS */
    /**
     * For a given user, return a list of Transactions he has sent, in reversed order, so the last transaction comes first.
     * @param sender the User who send these transactions.
     * @return a List of Transaction object in reverse order.
     */
    public List<Transaction> getAllTransactionsBySender(User sender) {
        logger.info("Fetching all transactions from sender: " + sender.getUsername());
        return transactionRepository.findBySender(sender).reversed();
    }

    /**
     * Helper method to check if the transaction is valid, that is to say if the sender has enough money in its wallet.
     * @param transaction the Transaction object to check.
     * @return boolean.
     */
    private boolean isTransactionValid(Transaction transaction) {
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

    /**
     * Save a new Transaction in the database after checking if it's valid. Then it updates the amount of wallets of the sender and receiver.
     * If it succeeds, the method "commits" the new Transaction and the wallet's amount, and it's saved in the database.
     * If something wrong happens, everything "rollback" so any change impacts the database.
     * @param sender the User that send the money.
     * @param buddyEmail a String representing the email of the receiver.
     * @param description a String describing the Transaction's purpose.
     * @param amount a double representing the total money send.
     */
    @Transactional
    public void saveNewTransaction(User sender, String buddyEmail, String description, double amount) {
        User receiver = userService.findUserByEmail(buddyEmail);

        Transaction transaction = new Transaction(sender, receiver, description, amount);

        if(isTransactionValid(transaction)) {
            userService.updateWallet(sender, sender.getWallet() - transaction.getAmount());
            logger.fine(sender.getUsername() + "'s new balance: " + sender.getWallet());

            userService.updateWallet(receiver, receiver.getWallet() + transaction.getAmount());
            logger.fine(receiver.getUsername() + "'s new balance: " + receiver.getWallet());

            logger.info("Saving transaction: " + transaction.getDescription() + " from " + sender.getUsername() + " to " + receiver.getUsername());
            transactionRepository.save(transaction);
        } else {
            logger.warning("Transaction could not be saved");
            throw new RuntimeException("Transaction could not be saved for the amount: " + amount + " and a wallet of " + sender.getWallet());
        }
    }
}