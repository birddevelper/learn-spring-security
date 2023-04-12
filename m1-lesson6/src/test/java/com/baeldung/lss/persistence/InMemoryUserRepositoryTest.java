package com.baeldung.lss.persistence;
import com.baeldung.lss.spring.LssApp6;
import com.baeldung.lss.web.model.User;
import com.google.common.collect.Iterables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = LssApp6.class)
class InMemoryUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        // clear the repository after each test
        userRepository.deleteAll();
    }

    @Test
    void testFindAll_whenRepositoryIsEmpty_thenReturnEmptyIterable() {
        Iterable<User> users = userRepository.findAll();
        assertEquals(0, Iterables.size(users));
    }

    @Test
    void testFindAll_whenRepositoryHasTwoUser_thenReturnIterableWithSizOfTwo() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);

        User user2 = new User();
        user1.setUsername("user2");
        user1.setEmail("user2@com.com");
        userRepository.save(user2);
        // As there exist only 2 user in the repository, findAll() should return both of them
        Iterable<User> users = userRepository.findAll();
        assertEquals(2, Iterables.size(users));
    }

    @Test
    void testSave() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);


        Iterable<User> users = userRepository.findAll();
        assertEquals(1, Iterables.size(users));
    }

    @Test
    void testFindUser_whenUserIdExist_thenReturnTheUser() {
        User user1 = new User();
        user1.setId(120L);
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);

        User user = userRepository.findUser(120L);
        assertEquals(120L, user.getId());
        assertEquals("user1",user1.getUsername());
        assertEquals("user1@com.com",user.getEmail());
    }

    @Test
    void testFindUser_whenUserIdDoesNotExist_thenReturnNull() {
        // No user with id=10 exists
        User user = userRepository.findUser(10L);
        assertNull(user);
    }

    @Test
    void testDeleteUser_whenUserIdExists_thenMustDeleteTheUser() {
        User user1 = new User();
        user1.setId(12L);
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);
        // Delete existing user
        userRepository.deleteUser(12L);

        Iterable<User> users = userRepository.findAll();
        // As there was only one user in the repository,
        // After deleting user1 the repository should be empty
        assertEquals(0, Iterables.size(users));
    }

    @Test
    void testDeleteUser_whenUserIdDoesNotExistExists_thenDeleteNoUsers() {
        User user1 = new User();
        user1.setId(12L);
        user1.setUsername("user1");
        user1.setEmail("user1@com.com");
        userRepository.save(user1);
        // Delete user which does not exist
        userRepository.deleteUser(11L);

        Iterable<User> users = userRepository.findAll();
        // The user1 should be remained intact
        assertEquals(1, Iterables.size(users));
    }
}