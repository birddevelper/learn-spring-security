package com.baeldung.lss.web.controller;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.spring.LssApp6;
import com.baeldung.lss.web.model.User;
import com.google.common.collect.Iterables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = LssApp6.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    @WithMockUser(username="user", roles={"USER"})
    void testList_whenUserIsLoggedIn_thenShowTheUserListPage() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Users : View all")));
    }

    @Test
    void testList_whenUserIsNotLoggedIn_thenRedirectToLoginPage() throws Exception {
        // UnAuthenticated users must be redirected to login page
        MvcResult result = mockMvc.perform(get("/user"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/login"));
    }

    @Test
    @WithMockUser(username="user", roles={"USER"})
    void testView_whenUserIsLoggedIn_thenShowUserInfoPage() throws Exception {
        User user1 = new User();
        user1.setId(12L);
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);


        mockMvc.perform(get("/user/12"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("user1")));
    }

    @Test
    void testView_whenUserIsNotLoggedIn_thenRedirectToLoginPage() throws Exception {
        User user1 = new User();
        user1.setId(12L);
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);

        MvcResult result = mockMvc.perform(get("/user/12"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        // UnAuthenticated users must be redirected to login page
        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/login"));
    }

    @Test
    @WithMockUser(username="user", roles={"USER"})
    void testCreate_whenUserIsLoggedIn_thenCreateUserAndRedirectToView() throws Exception {
        User user = new User();
        user.setUsername("user25");
        user.setEmail("user25@example.com");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", user.getUsername())
                        .param("email", user.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/user/*"));

        Iterable<User> users = userRepository.findAll();
        assertEquals(1, Iterables.size(users));
    }

    @Test
    void testCreate_whenUserIsNotLoggedIn_thenRedirectToLoginPage() throws Exception {

        User user = new User();
        user.setUsername("user25");
        user.setEmail("user25@example.com");

        MvcResult result = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", user.getUsername())
                .param("email", user.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/login"));
    }

    @Test
    @WithMockUser(username="user", roles={"USER"})
    void testDelete_whenUserIsLoggedIn_thenDeleteTheUser() throws Exception {
        User user = new User();
        user.setId(25L);
        user.setUsername("user25");
        user.setEmail("user25@example.com");
        userRepository.save(user);

        MvcResult result = mockMvc.perform(get("/user/delete/25"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/user"));

        assertNull(userRepository.findUser(25L));
    }

    @Test
    void testDelete_whenUserIsNotLoggedIn_thenRedirectToLoginPage() throws Exception {
        User user = new User();
        user.setId(25L);
        user.setUsername("user25");
        user.setEmail("user25@example.com");
        userRepository.save(user);

        MvcResult result = mockMvc.perform(get("/user/delete/25"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/login"));
        // The user should not be removed
        assertNotNull(userRepository.findUser(25L));
    }


    @Test
    @WithMockUser(username="user", roles={"USER"})
    void testModifyForm_whenUserIsLoggedIn_thenDisplayEditForm() throws Exception {

        User user1 = new User();
        user1.setId(12L);
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);

        mockMvc.perform(get("/user/modify/12"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("user1")));
    }

    @Test
    void testModifyForm_whenUserIsNotLoggedIn_thenRedirectToLoginPage() throws Exception {

        User user1 = new User();
        user1.setId(12L);
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);

        MvcResult result = mockMvc.perform(get("/user/modify/12"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/login"));

    }

    @Test
    @WithMockUser(username="user", roles={"USER"})
    void testCreateForm_whenUserIsLoggedIn_thenDisplayCreateForm() throws Exception {
        mockMvc.perform(get("/user/?form"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Users : Create")));
    }

    @Test
    void testCreateForm_whenUserIsNotLoggedIn_thenRedirectToLoginPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/?form"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getHeader("Location");
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.contains("/login"));
    }
}