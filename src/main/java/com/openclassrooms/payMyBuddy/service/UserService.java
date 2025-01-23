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

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    private final Logger logger = Logger.getLogger(UserService.class.getName());

    public User findUserByEmail(String email) {
        logger.info("Finding user by email: " + email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warning("No user found with email: " + email);
        } else {
            logger.info("Found user: " + user.getUsername());
        }
        return user;
    }

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

    public Set<User> getAllMyBuddies(User sender) {
        logger.info("fetching all buddies from " + sender.getUsername());
        return sender.getBuddies();
    }

    public List<String> getAllMyBuddyNames(User sender) {
        List<String> buddiesNames = new ArrayList<>();
        for (User buddy : getAllMyBuddies(sender)) {
            buddiesNames.add(buddy.getUsername());
        }
        logger.info(buddiesNames.size() + " buddies names found from a list of " + getAllMyBuddyNames(sender).size() + "buddies");
        return buddiesNames;
    }

    public void updateUser(User updatedUser, User currentUser) {
        boolean isModified = false;

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty() && !updatedUser.getUsername().equals(currentUser.getUsername())) {
            logger.info("Username changed: " + updatedUser.getUsername());
            currentUser.setUsername(updatedUser.getUsername());
            isModified = true;
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty() && !updatedUser.getEmail().equals(currentUser.getEmail())) {
            logger.info("Email changed: " + updatedUser.getEmail());
            currentUser.setEmail(updatedUser.getEmail());
            isModified = true;
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            logger.info("Password changed");
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

    public void updateWallet(User user, double newWallet) {
        user.setWallet(newWallet);
        userRepository.save(user);
    }

    public void saveNewUser(User user) {
        if (!isEmailUnique(user.getEmail(), user.getId())) {
            throw new IllegalArgumentException("Email already exists : " + user.getEmail());
        }

        logger.info("Saving user: " + user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("No authenticated user found");
        }
        int currentUserId = Integer.parseInt(authentication.getName());
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("User with id " + currentUserId + " not found"));
    }

    public boolean isEmailUnique(String email, Integer userId) {
        logger.fine("Checking if email is unique: " + email);
        User existingUserWithThisEmail = userRepository.findByEmail(email);
        return existingUserWithThisEmail == null || existingUserWithThisEmail.getId() == (userId);
    }

    /**
     * Gestion de la suppression d'un utilisateur
     * Retire l'utilisateur de la liste des buddies pour chacun de ses buddies.
     * Puis supprime sa liste de buddies.
     * Enfin, supprime l'utilisateur.
     * @param user l'utilisateur à supprimer
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
