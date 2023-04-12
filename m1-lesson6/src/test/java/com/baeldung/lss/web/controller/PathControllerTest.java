package com.baeldung.lss.web.controller;

import com.baeldung.lss.spring.LssApp6;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LssApp6.class)
@AutoConfigureMockMvc
class PathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username="user", roles={"USER"})
    void testHome_whenUserIsLoggedIn_thenRedirectToUserPage() throws Exception {

        // If user requesting root path of app is authenticated, it should be redirected to /user endpoint
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/user"));
    }


    @Test
    void testHome_whenUserIsNotLoggedIn_thenRedirectToLoginPage() throws Exception {
        // If user requesting root path of app is not authenticated, it should be redirected to /login endpoint
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/login"));
    }
}