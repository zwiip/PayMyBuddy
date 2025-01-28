package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser
    void testGetTransfer() throws Exception {
        User currentUser = new User("Marilla", "marilla@cuthbert.com", "avonlea");
        when(userService.getCurrentUser()).thenReturn(currentUser);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }

    @Test
    @WithMockUser
    void testPostProfile() throws Exception {
        User currentUser = new User("Marilla", "marilla@cuthbert.com", "avonlea");
        User updatedUser = new User("Marilla", "marilla@cuthbert.com", "newPassword");
        when(userService.getCurrentUser()).thenReturn(currentUser);

        mockMvc.perform(post("/profile").with(csrf())
                .flashAttr("user", updatedUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"));
    }
}
