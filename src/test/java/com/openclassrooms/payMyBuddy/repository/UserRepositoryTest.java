package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("John", "john@gmail.com", "1234");
        userRepository.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(testUser);
    }

    @Test
    void givenUser_whenSaved_thenCanBeFoundById() {
        User savedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(savedUser);
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        assertEquals(testUser.getPassword(), savedUser.getPassword());
    }

    @Test
    public void givenUser_whenFindUserByEmailCalled_thenUserIsFound() {
        User foundUser = userRepository.findByEmail("john@gmail.com");

        assertNotNull(foundUser);
        assertEquals("john@gmail.com", foundUser.getEmail());
    }

    @Test
    void givenUser_whenUpdated_thenCanBeFoundByIdWithUpdatedData() {
        testUser.setUsername("Johnny");
        userRepository.save(testUser);

        User UpdatedUser = userRepository.findById(testUser.getId()).orElse(null);

        assertNotNull(UpdatedUser);
        assertEquals("Johnny", UpdatedUser.getUsername());
    }

    @Test
    void givenUser_whenDeleted_thenCannotBeFoundById() {
        userRepository.delete(testUser);

        User DeletedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNull(DeletedUser);
    }

    @Test
    void givenUser_whenDeletedById_thenCannotBeFoundById() {
        userRepository.deleteById(testUser.getId());
        User DeletedUser = userRepository.findById(testUser.getId()).orElse(null);

        assertNull(DeletedUser);
    }

    @Test
    void givenSeveralUsers_whenDeleteAll_thenNoMoreUserInRepository() {
        User anotherUser = new User("Jane", "jane@gmail.com", "4321");
        userRepository.save(anotherUser);
        assertEquals(2, userRepository.count());

        userRepository.deleteAll();

        assertEquals(0, userRepository.count());
    }
}