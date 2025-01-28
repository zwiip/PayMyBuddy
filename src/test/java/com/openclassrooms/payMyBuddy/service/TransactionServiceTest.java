package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    private UserService userService;

    private Transaction transaction, newTransaction, anotherTransaction;
    private TransactionService transactionService;
    private User sender, receiver;

    @BeforeEach
    public void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        userService = mock(UserService.class);
        transactionService = new TransactionService(transactionRepository, userService);

        sender = new User("Sender", "sender@gmail.com", "1234");
        receiver = new User("Receiver", "receiver@gmail.com", "4321");
        newTransaction = new Transaction(sender, receiver, "a tiny description", 25.50);
        anotherTransaction = new Transaction(sender, receiver, "another description", 52);
    }

    @Test
    public void givenTwoTransactions_whenGetAllTransactionsBySender_thenReturnTwoTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(newTransaction);
        transactions.add(anotherTransaction);
        when(transactionRepository.findBySender(sender)).thenReturn(transactions);

        List<Transaction> actualTransactions = transactionService.getAllTransactionsBySender(sender);

        assertEquals(2, actualTransactions.size());
        assertTrue(actualTransactions.contains(newTransaction));
        assertTrue(actualTransactions.contains(anotherTransaction));
    }
//
//    @Test
//    public void givenANewTransaction_whenSaveNewTransaction_thenTransactionSaved() {
//        transactionService.saveNewTransaction(sender, "receiver@gmail.com", "description", 30);
//
//        verify(transactionRepository, times(1)).save(any(Transaction.class));
//    }



}
