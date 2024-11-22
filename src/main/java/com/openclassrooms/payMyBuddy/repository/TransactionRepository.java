package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySender(User sender);

    List<Transaction> findByReceiver(User receiver);

    List<Transaction> findBySenderAndReceiver(User sender, User receiver);
}
