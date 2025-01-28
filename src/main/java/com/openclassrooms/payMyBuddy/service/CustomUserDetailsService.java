package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    /* VARIABLE*/
    private final UserService userService;

    /* CONSTRUCTOR */
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Used by Spring Security to retrieve the user's details during the authentication.
     * Fetches the user from the database using the provided email, and if the user exists, create the userDetails
     * @param email the email address of the user to load.
     * @return a UserDetails object containing the user's authentication details.
     * @throws UsernameNotFoundException if no user is found with the provided email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(String.valueOf(user.getId()))
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}