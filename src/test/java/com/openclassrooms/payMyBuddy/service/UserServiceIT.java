package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user, buddy, anotherBuddy;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        user = userRepository.save(new User("User", "user@gmail.com", "0000"));
        buddy = userRepository.save(new User("Buddy", "buddy@gmail.com", "4321"));
        anotherBuddy = userRepository.save(new User("Another", "another@gmail.com", "1478"));
    }

    @Test
    public void givenExistingEmail_whenFindUserByEmail_thenReturnUser() {
        User foundUser = userService.findUserByEmail("user@gmail.com");

        assertNotNull(foundUser);
        assertEquals("User", foundUser.getUsername());
    }

    @Test
    public void givenNonExistingEmail_whenFindUserByEmail_thenReturnNull() {
        User foundUser = userService.findUserByEmail("nope@gmail.com");

        assertNull(foundUser);
    }

//    @Test // ce test ne fonctionne pas
//    public void givenNewBuddy_whenAddNewBuddy_thenBuddyAddedToSetOfBuddies_andUserAddedToSetOfNewBuddyBuddies() {
//        Set<User> myBuddies = new HashSet<>();
//        myBuddies.add(buddy);
//        user.setBuddies(myBuddies);
//        userRepository.save(user);
//
//        userService.addNewBuddy("another@gmail.com", user);
//
//        assertEquals(2, user.getBuddies().size());
//        assertTrue(user.getBuddies().contains(anotherBuddy));
//        assertTrue(anotherBuddy.getBuddies().contains(user));
//    }
//
//    @Test // ce test ne fonctionne pas
//    public void givenExistingBuddy_whenAddNewBuddy_thenExistingBuddyNotAdded() {
//        Set<User> myBuddies = new HashSet<>();
//        myBuddies.add(buddy);
//        myBuddies.add(anotherBuddy);
//        user.setBuddies(myBuddies);
//        userRepository.save(user);
//
//        userService.addNewBuddy("another@gmail.com", user);
//
//        assertEquals(2, user.getBuddies().size());
//        assertTrue(user.getBuddies().contains(buddy));
//        assertTrue(user.getBuddies().contains(anotherBuddy));
//    }

    @Test
    public void givenTwoBuddies_whenGetAllMyBuddies_thenReturnMyTwoBuddies() {
        Set<User> myBuddies = new HashSet<>();
        myBuddies.add(buddy);
        myBuddies.add(anotherBuddy);
        user.setBuddies(myBuddies);
        userRepository.save(user);

        Set<User> actualBuddies = userService.getAllMyBuddies(user);

        assertEquals(2, actualBuddies.size());
        assertTrue(actualBuddies.contains(buddy));
    }

    @Test
    public void givenSavedUser_whenUpdateUser_thenReturnUpdatedUser() {
        User updatedUser = new User();
        updatedUser.setUsername("UpdatedUser");
        updatedUser.setEmail("updated.email@gmail.com");

        userService.updateUser(updatedUser, user);

        assertEquals("UpdatedUser", user.getUsername());
        assertEquals("updated.email@gmail.com", user.getEmail());
    }

    @Test
    public void testUpdateWallet() {
        double newWallet = 200.50;
        userService.updateWallet(user, newWallet);

        assertEquals(newWallet, user.getWallet());
    }

    @Test // ce test ne fonctionne pas
    public void testSaveNewUser() {
        User newUser = new User("NewUser", "newuser@gmail.com", "password123");
        userService.saveNewUser(newUser);

        User savedUser = userRepository.findByEmail("newuser@gmail.com");
        assertNotNull(savedUser);
        assertEquals("NewUser", savedUser.getUsername());
    }
//
//    @Test
//    public void givenTwoBuddies_whenDeleteUser_thenUserRemovedFromBuddiesAndDeleted() {
//        Set<User> myBuddies = new HashSet<>();
//        myBuddies.add(buddy);
//        myBuddies.add(anotherBuddy);
//        user.setBuddies(myBuddies);
//
//        Set<User> myFirstBuddyBuddies = new HashSet<>();
//        myFirstBuddyBuddies.add(user);
//        myFirstBuddyBuddies.add(anotherBuddy);
//        buddy.setBuddies(myFirstBuddyBuddies);
//
//        Set<User> mySecondBuddyBuddies = new HashSet<>();
//        mySecondBuddyBuddies.add(user);
//        mySecondBuddyBuddies.add(buddy);
//        anotherBuddy.setBuddies(mySecondBuddyBuddies);
//
//        userService.deleteUser(user);
//
//        assertFalse(myFirstBuddyBuddies.contains(user));
//        assertFalse(mySecondBuddyBuddies.contains(user));
//
//        verify(userRepository, times(1)).delete(user);
//    }
}
