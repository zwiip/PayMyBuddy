package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transactionOne, transactionTwo, transactionThree;
    private User anne;
    private User diana;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        anne = new User("aShirley", "anne.shirley@greengables.com", "anne123");
        diana = new User("dBarry", "diana.barry@orchardslope.com", "diana456");
        userRepository.save(anne);
        userRepository.save(diana);

        transactionOne = new Transaction(anne, diana, "Remboursement vase", 20.50);
        transactionTwo = new Transaction(diana, anne, "Participation pique nique", 10.35);
        transactionRepository.save(transactionOne);
        transactionRepository.save(transactionTwo);
    }

    @AfterEach
    public void tearDown() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void givenTransaction_whenSaved_thenCanBeFoundById() {
        Transaction savedTransaction = transactionRepository.findById(transactionOne.getId()).orElse(null);
        assertNotNull(savedTransaction);
        assertEquals(20.50, savedTransaction.getAmount());
    }

    @Test
    public void givenTwoTransactionSaved_whenFindAll_thenFoundTwoTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        assertEquals(2, transactions.size());
    }

    @Test
    public void givenSavedTransaction_whenFindBySender_thenTransactionFound() {
        List<Transaction> savedTransaction = transactionRepository.findBySender(anne);
        assertNotNull(savedTransaction);
        assertEquals(20.50, savedTransaction.getFirst().getAmount());
    }

    @Test
    public void givenSavedTransaction_whenFindByReceiver_thenTransactionFound() {
        List<Transaction> savedTransaction = transactionRepository.findByReceiver(anne);
        assertNotEquals(0, savedTransaction.size());
        assertEquals(10.35, savedTransaction.getFirst().getAmount());
    }

    @Test
    public void givenSavedTransaction_whenFindBySenderAndReceiver_thenTransactionFound() {
        List<Transaction> savedTransaction = transactionRepository.findBySenderAndReceiver(diana, anne);
        assertNotEquals(0, savedTransaction.size());
        assertEquals(10.35, savedTransaction.getFirst().getAmount());
    }
}
