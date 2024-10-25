package com.openclassrooms.payMyBuddy.dataLayer.repository;

import com.openclassrooms.payMyBuddy.dataLayer.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository extends CrudRepository<Transaction, Integer> {

}
