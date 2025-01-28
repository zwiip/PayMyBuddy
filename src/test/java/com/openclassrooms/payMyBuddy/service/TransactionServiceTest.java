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


    @Test
    public void givenValidTransaction_whenSaveNewTransaction_thenUpdateWalletsAndSaveTransaction() {
        // Arrange
        sender.setWallet(100.00);
        receiver.setWallet(50.00);
        newTransaction = new Transaction(sender, receiver, "Payment for services", 25.50);

        when(userService.findUserByEmail(receiver.getEmail())).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(newTransaction);
        doNothing().when(userService).updateWallet(any(User.class), anyDouble());

        // Act
        transactionService.saveNewTransaction(sender, receiver.getEmail(), newTransaction.getDescription(), newTransaction.getAmount());

        // Assert
        verify(userService, times(1)).updateWallet(sender, 74.50); // 100 - 25.50
        verify(userService, times(1)).updateWallet(receiver, 75.50);

    }

    @Test
    public void givenNonExistentReceiver_whenSaveNewTransaction_thenThrowException() {
        // Arrange
        when(userService.findUserByEmail("nonexistent@gmail.com")).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> transactionService.saveNewTransaction(sender, "nonexistent@gmail.com", "Description", 10.00));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
