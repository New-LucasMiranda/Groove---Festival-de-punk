package br.anhembi.groove_aplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import br.anhembi.groove_aplication.entities.User;
import br.anhembi.groove_aplication.exception.InvalidUserException;
import br.anhembi.groove_aplication.exception.UserNotFoundException;
import br.anhembi.groove_aplication.repository.UserRepo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserService Tests")
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        testUser = new User("12345678901", "John Doe", 25, "john@example.com", "1", null, null, true);
    }

    @Test
    @DisplayName("Should create a new user successfully")
    void testInsertNewUser() {
        User created = userService.insert(testUser);

        assertNotNull(created);
        assertEquals("12345678901", created.getCpf());
        assertEquals("John Doe", created.getNome());
        assertTrue(created.getSituacao());
    }

    @Test
    @DisplayName("Should throw exception when inserting user with null CPF")
    void testInsertUserWithNullCpf() {
        testUser.setCpf(null);
        assertThrows(InvalidUserException.class, () -> userService.insert(testUser));
    }

    @Test
    @DisplayName("Should throw exception when inserting user with null email")
    void testInsertUserWithNullEmail() {
        testUser.setEmail(null);
        assertThrows(InvalidUserException.class, () -> userService.insert(testUser));
    }

    @Test
    @DisplayName("Should get user by CPF successfully")
    void testGetUserByCpf() {
        userService.insert(testUser);
        Optional<User> retrieved = userService.getUser("12345678901");

        assertTrue(retrieved.isPresent());
        assertEquals("John Doe", retrieved.get().getNome());
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent user")
    void testGetUserNotFound() {
        assertThrows(UserNotFoundException.class, () ->
                userService.getUser("00000000000").orElseThrow(() -> new UserNotFoundException("00000000000")));
    }

    @Test
    @DisplayName("Should update primary reservation successfully")
    void testUpdatePrimReserva() {
        userService.insert(testUser);
        User updated = userService.updatePrimReserva("12345678901", "Pista");

        assertNotNull(updated);
        assertEquals("Pista", updated.getPrimReserva());
    }

    @Test
    @DisplayName("Should update secondary reservation successfully")
    void testUpdateSegReserva() {
        userService.insert(testUser);
        User updated = userService.updateSegReserva("12345678901", "Camarote");

        assertNotNull(updated);
        assertEquals("Camarote", updated.getSegReserva());
    }

    @Test
    @DisplayName("Should update user situation successfully")
    void testUpdateSituacao() {
        userService.insert(testUser);
        User updated = userService.updateSituacao("12345678901", false);

        assertNotNull(updated);
        assertFalse(updated.getSituacao());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        userService.insert(testUser);
        boolean deleted = userService.delete("12345678901");

        assertTrue(deleted);
        assertFalse(userRepo.existsById("12345678901"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> userService.delete("00000000000"));
    }

    @Test
    @DisplayName("Should create VIP user successfully")
    void testInsertVIPUser() {
        testUser.setDia("VIP");
        testUser.setSituacao(false);
        testUser.setPrimReserva("VIP");
        testUser.setSegReserva("VIP");

        User created = userService.insertVIP(testUser);

        assertNotNull(created);
        assertEquals("VIP", created.getDia());
    }

    @Test
    @DisplayName("Should get all users")
    void testGetAllUsers() {
        userService.insert(testUser);
        User user2 = new User("98765432101", "Jane Doe", 30, "jane@example.com", "2", null, null, true);
        userService.insert(user2);

        var users = userService.getAllUsers();
        assertEquals(2, users.size());
    }
}
