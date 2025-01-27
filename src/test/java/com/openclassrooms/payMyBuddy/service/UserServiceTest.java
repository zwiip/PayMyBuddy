package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private User user, buddy, anotherBuddy;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);

        user = new User("User", "user@gmail.com", "0000");
        buddy = new User("Buddy", "buddy@gmail.com", "4321");
        anotherBuddy = new User("Another", "another@gmail.com", "1478");
    }


    @Test
    void givenExistingEmail_whenFindUserByEmail_thenReturnUser() {
        User user = new User("User", "user@gmail.com", "0000");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(user);

        User foundUser = userService.findUserByEmail("user@gmail.com");

        assertNotNull(foundUser);
        assertEquals("User", foundUser.getUsername());
        verify(userRepository, times(1)).findByEmail("user@gmail.com");
    }

    @Test
    public void givenNewBuddy_whenAddNewBuddy_thenBuddyAddedToSetOfBuddies_andUserAddedToSetOfNewBuddyBuddies() {
        when(userRepository.findByEmail("another@gmail.com")).thenReturn(anotherBuddy);

        userService.addNewBuddy("another@gmail.com", user);

        assertTrue(user.getBuddies().contains(anotherBuddy));
        assertTrue(anotherBuddy.getBuddies().contains(user));
    }

    @Test
    public void givenExistingBuddy_whenAddNewBuddy_thenExistingBuddyNotAdded() {
        Set<User> myBuddies = new HashSet<>();
        myBuddies.add(buddy);
        myBuddies.add(anotherBuddy);
        user.setBuddies(myBuddies);
        when(userRepository.findByEmail("another@gmail.com")).thenReturn(anotherBuddy);


        userService.addNewBuddy("another@gmail.com", user);

        assertEquals(2, user.getBuddies().size());
        assertTrue(user.getBuddies().contains(buddy));
        assertTrue(user.getBuddies().contains(anotherBuddy));
    }

    @Test
    public void givenTwoBuddies_whenGetAllMyBuddies_thenReturnMyTwoBuddies() {
        Set<User> myBuddies = new HashSet<>();
        myBuddies.add(buddy);
        myBuddies.add(anotherBuddy);
        user.setBuddies(myBuddies);

        Set<User> actualBuddies = userService.getAllMyBuddies(user);

        assertEquals(2, actualBuddies.size());
        assertTrue(actualBuddies.contains(buddy));
    }

    @Test
    public void givenCorrectInputs_whenSaveNewUser_thenUserIsSaved() {
        User newUser = new User("NewUser", "newuser@gmail.com", "password123");

        when(userRepository.save(newUser)).thenReturn(newUser);
        when(userRepository.findByEmail("newuser@gmail.com")).thenReturn(newUser);

        userService.saveNewUser(newUser);

        User savedUser = userRepository.findByEmail("newuser@gmail.com");
        assertNotNull(savedUser);
        assertEquals("NewUser", savedUser.getUsername());
    }

}
