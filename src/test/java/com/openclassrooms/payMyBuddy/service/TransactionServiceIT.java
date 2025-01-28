package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionServiceIT {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    private Transaction transaction, newTransaction, anotherTransaction;
    private User sender, receiver;

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll();
        sender = userRepository.save(new User("Sender", "sender@gmail.com", "1234"));
        receiver = userRepository.save(new User("Receiver", "receiver@gmail.com", "4321"));
        newTransaction = transactionRepository.save(new Transaction(sender, receiver, "a tiny description", 25.50));
        anotherTransaction = transactionRepository.save(new Transaction(sender, receiver, "another description", 52));
    }

    @Test
    public void givenDeletedReceiver_whenGetAllTransactionsBySender_thenReturnNullAsReceiver() {
        transactionRepository.save(new Transaction(sender, receiver, "Description 1", 50.0));
        transactionRepository.save(new Transaction(sender, receiver, "Description 2", 30.0));

        // Act
        userService.deleteUser(receiver);

        // Assert
        List<Transaction> foundTransactions = transactionRepository.findBySender(sender);
        assertNull(foundTransactions.getFirst().getReceiver());
    }

    @Test
    public void givenANewTransaction_whenSaveNewTransaction_thenTransactionSaved() {
        transactionService.saveNewTransaction(sender, "receiver@gmail.com", "description", 30);


    }
}
