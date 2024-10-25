package com.openclassrooms.payMyBuddy.dataLayer.repository;

import com.openclassrooms.payMyBuddy.dataLayer.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends CrudRepository<User, Integer> {

}
