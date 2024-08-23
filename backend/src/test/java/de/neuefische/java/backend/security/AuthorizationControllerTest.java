package de.neuefische.java.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
//    @WithMockUser
    void getLoggedInUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
                        .with(oidcLogin()
                                .idToken(token -> token.subject("123"))
                                .userInfoToken(token -> token.claim("login", "Floooooooooorian"))))
                .andExpect(status().isOk())
                .andExpect(content().string("123"));
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void addTodo() throws Exception {
        mockMvc.perform(post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "description": "abc",
                                    "status": "OPEN"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
