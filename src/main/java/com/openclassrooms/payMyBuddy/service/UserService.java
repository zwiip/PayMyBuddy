package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    // TODO vérifier usage
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    // TODO vérifier usage
    public Optional<User> getUser(Integer id) {
        return userRepository.findById(id);
    }

    public User findUserByEmail(String email) {
        logger.info("Finding user by email: " + email);
        return userRepository.findByEmail(email);
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

    public User updateUser(User updatedUser, User currentUser) {
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
            return userRepository.save(currentUser);
        } else {
            logger.warning("No change found for user: " + currentUser.getUsername());
            return currentUser;
        }
    }

    public User saveUser(User user) {
        logger.info("Saving user: " + user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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
}
