package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public void addNewBuddy(String email, User user) {
        Set<User> myBuddies = user.getBuddies();
        User newBuddy = userRepository.findByEmail(email);
        if (newBuddy != null) {
            // setting the buddies list if null
            if (myBuddies == null) {
                user.setBuddies(new HashSet<>());
            }
            myBuddies.add(newBuddy);
            newBuddy.getBuddies().add(user);
            userRepository.save(user);
            userRepository.save(newBuddy);
        } else {
            logger.warning("Nobody found with email: " + email + " while adding a buddy for user: " + user.getUsername());
        }
    }

    Set<User> getAllMyBuddies(User sender) {
        return sender.getBuddies();
    }

    List<String> getAllMyBuddyNames(User sender) {
        List<String> buddiesNames = new ArrayList<>();
        for (User buddy : getAllMyBuddies(sender)) {
            buddiesNames.add(buddy.getUsername());
        }
        return buddiesNames;
    }

    User updateUser(String username, String email, String password, User user) {
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return userRepository.save(user);
    }
}
