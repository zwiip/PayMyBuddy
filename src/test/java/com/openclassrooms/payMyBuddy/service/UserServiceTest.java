package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private User user;

    private UserService userService;
    private User buddy, anotherBuddy;

    Set<User> myBuddies = new HashSet<>();

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        user = mock(User.class);
        userService = new UserService(userRepository);

        buddy = new User("Buddy", "buddy@gmail.com", "4321");
        anotherBuddy = new User("Another", "another@gmail.com", "1478");

        myBuddies.add(buddy);
        myBuddies.add(anotherBuddy);
    }

    @Test
    public void givenNewBuddy_whenAddNewBuddy_thenBuddyAddedToSetOfBuddies_andUserAddedToSetOfNewBuddyBuddies() {
        User newBuddy = new User("New Buddy", "new.buddy@gmail.com", "1478");
        when(user.getBuddies()).thenReturn(myBuddies);
        when(userRepository.findByEmail("new.buddy@gmail.com")).thenReturn(newBuddy);

        userService.addNewBuddy("new.buddy@gmail.com", user);

        assertEquals(3, user.getBuddies().size());
        assertTrue(user.getBuddies().contains(buddy));
        assertTrue(newBuddy.getBuddies().contains(user));
    }

    @Test
    public void givenExistingBuddy_whenAddNewBuddy_thenExistingBuddyNotAdded() {
        when(userRepository.findByEmail("another@gmail.com")).thenReturn(anotherBuddy);
        when(user.getBuddies()).thenReturn(myBuddies);

        userService.addNewBuddy("another@gmail.com", user);

        assertEquals(2, user.getBuddies().size());
        assertTrue(user.getBuddies().contains(buddy));
        assertTrue(user.getBuddies().contains(anotherBuddy));
    }

    @Test
    public void givenTwoBuddies_whenGetAllMyBuddies_thenReturnMyTwoBuddies() {
        when(user.getBuddies()).thenReturn(myBuddies);

        Set<User> actualBuddies = userService.getAllMyBuddies(user);

        assertEquals(2, actualBuddies.size());
        assertTrue(actualBuddies.contains(buddy));
    }

    @Test
    public void givenTwoBuddies_whenGetAllMyBuddyNames_thenReturnMyTwoBuddyNames() {
        when(user.getBuddies()).thenReturn(myBuddies);

        List<String> actualBuddyNames = userService.getAllMyBuddyNames(user);

        assertEquals(2, actualBuddyNames.size());
        assertTrue(actualBuddyNames.contains("Buddy"));
        assertTrue(actualBuddyNames.contains("Another"));
    }

    @Test
    public void givenSavedUser_whenUpdateUser_thenReturnUpdatedUser() {
        when(userRepository.save(buddy)).thenReturn(buddy);

        userService.updateUser(new User("UpdatedUser", "updated.email@gmail.com", "2589"), buddy);

        verify(userRepository, times(1)).save(buddy);
        assertEquals("UpdatedUser", buddy.getUsername());
        assertEquals("updated.email@gmail.com", buddy.getEmail());
    }

    @Test
    public void givenNewCorrectUser_whenSaveUser_thenReturnSavedUser() {
        User newBuddy = new User("New Buddy", "new.buddy@gmail.com", "1478");

        when(userRepository.findByEmail("new.buddy@gmail.com")).thenReturn(null);
        User savedBuddy = userService.saveUser(newBuddy);

        verify(userRepository, times(1)).save(newBuddy);
        //assertEquals(newBuddy.getUsername(), savedBuddy.getUsername());
    }

    // TODO : getCurrentUser

    @Test
    public void givenUserWithAUniqueEmail_whenCheckingIfEmailIsUnique_thenReturnTrue() {
        when(userRepository.findByEmail("another@gmail.com")).thenReturn(anotherBuddy);
        assertTrue(userService.isEmailUnique("anotherEmail", anotherBuddy.getId()));
    }

    @Test
    public void givenUserWithAlreadyUsedEmail_whenCheckingIfEmailIsUnique_thenReturnFalse() {
        when(userRepository.findByEmail("another@gmail.com")).thenReturn(anotherBuddy);
        assertFalse(userService.isEmailUnique("another@gmail.com", 1000));
    }
}
