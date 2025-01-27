package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;

@Service
public class UserService {

    /* VARIABLES */
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger logger = Logger.getLogger(UserService.class.getName());

    /* CONSTRUCTOR */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Find the User matching this email.
     * @param email a String representing an email we want to search.
     * @return a User object or null if no user matches the given email.
     */
    public User findUserByEmail(String email) {
        logger.fine("Finding user by email: " + email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warning("No user found with email: " + email);
        } else {
            logger.info("Found user: " + user.getUsername());
        }
        return user;
    }

    /**
     * Fetch all the buddies of the given user to add a new one and add the user to the Buddy's buddies.
     * It's a Transactional method, so if everything's ok, the changes are commited to the database.
     * If not, everything rollbacks and nothing impacts the database.
     * @param email a String representing the email of the buddy to add.
     * @param user the given User who wants to add a new Buddy.
     */
    @Transactional
    public void addNewBuddy(String email, User user) {
        Set<User> myBuddies = user.getBuddies();
        User newBuddy = userRepository.findByEmail(email);

        if (newBuddy != null) {

            if (myBuddies == null) {
                user.setBuddies(new HashSet<>());
            }

            myBuddies.add(newBuddy);
            logger.info("New buddy added: " + newBuddy.getUsername() + " to user: " + user.getUsername());
            newBuddy.getBuddies().add(user);
            logger.info("New buddy added: " + user.getUsername() + " to user: " + newBuddy.getUsername());
            userRepository.save(user);
            userRepository.save(newBuddy);

        } else {
            logger.warning("Nobody found with email: " + email + " while adding a buddy for user: " + user.getUsername());
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }

    /**
     * Fetch all the buddies of the given User.
     * @param sender a User object.
     * @return a Set of User so no duplicates can be added.
     */
    public Set<User> getAllMyBuddies(User sender) {
        logger.info("fetching all buddies from " + sender.getUsername());
        return sender.getBuddies();
    }

    // TODO: vérifier si suppr getAllMyBuddyNames ok (supprimer des tests)

    /**
     * Checks which information has been modified before saving the User in the database.
     * @param updatedUser a User object with the new information.
     * @param currentUser the current User has saved in the database, on which we will set the new information.
     */
    public void updateUser(User updatedUser, User currentUser) {
        boolean isModified = false;

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty() && !updatedUser.getUsername().equals(currentUser.getUsername())) {
            logger.fine("Username changed: " + updatedUser.getUsername());
            currentUser.setUsername(updatedUser.getUsername());
            isModified = true;
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty() && !updatedUser.getEmail().equals(currentUser.getEmail())) {
            logger.fine("Email changed: " + updatedUser.getEmail());
            currentUser.setEmail(updatedUser.getEmail());
            isModified = true;
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            logger.fine("Password changed");
            currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            isModified = true;
        }

        if (isModified) {
            logger.info("Updating user: " + currentUser.getUsername());
            userRepository.save(currentUser);

        } else {
            logger.warning("No change found for user: " + currentUser.getUsername());
            throw new IllegalArgumentException("No change found for user: " + currentUser.getUsername());
        }
    }

    /**
     * Update the wallet amount of a given User.
     * @param user a User object.
     * @param newWallet the new amount of money to save for the User's wallet.
     */
    public void updateWallet(User user, double newWallet) {
        user.setWallet(newWallet);
        userRepository.save(user);
    }

    /**
     * Used to save a new user.
     * Check if the Email is unique in the database, then encode the password to save the new User in the database.
     * @param user a new User object to save in the database.
     */
    public void saveNewUser(User user) {
        if (!isEmailUnique(user.getEmail(), user.getId())) {
            throw new IllegalArgumentException("Email already exists : " + user.getEmail());
        }

        logger.info("Saving user: " + user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Return the User authenticated and using the app.
     * @return a User object.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("No authenticated user found");
        }
        int currentUserId = Integer.parseInt(authentication.getName());
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("User with id " + currentUserId + " not found"));
    }

    /**
     * A helper method to check if the given email already exists in the database for another User than the given one.
     * @param email a String representing the email to check.
     * @param userId the user's Id that is trying to add this email to his profile.
     * @return a boolean.
     */
    private boolean isEmailUnique(String email, Integer userId) {
        logger.fine("Checking if email is unique: " + email);
        User existingUserWithThisEmail = userRepository.findByEmail(email);
        return existingUserWithThisEmail == null || existingUserWithThisEmail.getId() == (userId);
    }

    /**
     * Deleting a User.
     * Delete the User from the buddies list of each buddies. Then delete his own buddies list.
     * Then delete the user.
     * If everything's ok, the changes are commited to the database.
     * If not, it rollbacks and the database isn't impacted.
     * @param user the User object to delete.
     */
    @Transactional
    public void deleteUser(User user) {
        for (User buddy : user.getBuddies()) {
            buddy.getBuddies().remove(user);
            logger.fine("Deleted user from buddy list: " + buddy.getUsername());
        }
        user.getBuddies().clear();
        logger.fine("Deleted buddy list of the user: " + user.getUsername());

        userRepository.delete(user);
        logger.info("Deleted user: " + user.getUsername());
    }
}