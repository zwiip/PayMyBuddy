package com.openclassrooms.payMyBuddy.controller;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    void testGetTransfer() throws Exception {
        mockMvc.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"));
    }

    @Test
    @WithMockUser
    void testPostTransfer() throws Exception {
        User currentUser = new User("Marilla", "marilla@cuthbert.com", "avonlea");
        Set<User> buddies = new HashSet<>();
        buddies.add(new User("Anne", "anne@shirley.com", "password"));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getAllMyBuddies(currentUser)).thenReturn(buddies);

        mockMvc.perform(post("/transfer").with(csrf())
                        .param("buddyEmail", "anne@shirley.com")
                        .param("description", "Payment for services")
                        .param("amount", "100.0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"));
    }
}
