package br.anhembi.groove_aplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.anhembi.groove_aplication.entities.User;
import br.anhembi.groove_aplication.repository.UserRepo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("UserController Tests")
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        testUser = new User("12345678901", "John Doe", 25, "john@example.com", "1", null, null, true);
    }

    @Test
    @DisplayName("GET /users should return all users")
    void testGetAllUsers() throws Exception {
        userRepo.save(testUser);

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /users/{cpf} should return user when found")
    void testGetUserByCpf() throws Exception {
        userRepo.save(testUser);

        mockMvc.perform(get("/users/12345678901").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", is("12345678901")))
                .andExpect(jsonPath("$.nome", is("John Doe")));
    }

    @Test
    @DisplayName("GET /users/{cpf} should return 404 when user not found")
    void testGetUserNotFound() throws Exception {
        mockMvc.perform(get("/users/00000000000").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /users should create new user")
    void testCreateUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf", is("12345678901")));
    }

    @Test
    @DisplayName("POST /users should return 400 for invalid user")
    void testCreateInvalidUser() throws Exception {
        User invalidUser = new User(null, "John", 25, "john@example.com", "1", null, null, true);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users/VIP should create VIP user")
    void testCreateVIPUser() throws Exception {
        testUser.setDia("VIP");
        testUser.setSituacao(false);
        testUser.setPrimReserva("VIP");
        testUser.setSegReserva("VIP");

        mockMvc.perform(post("/users/VIP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dia", is("VIP")));
    }

    @Test
    @DisplayName("PUT /users/{cpf}/prim-reserva should update primary reservation")
    void testUpdatePrimReserva() throws Exception {
        userRepo.save(testUser);

        mockMvc.perform(put("/users/12345678901/prim-reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Pista\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primReserva", is("Pista")));
    }

    @Test
    @DisplayName("PUT /users/{cpf}/seg-reserva should update secondary reservation")
    void testUpdateSegReserva() throws Exception {
        userRepo.save(testUser);

        mockMvc.perform(put("/users/12345678901/seg-reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Camarote\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.segReserva", is("Camarote")));
    }

    @Test
    @DisplayName("PUT /users/{cpf}/situacao should update user situation")
    void testUpdateSituacao() throws Exception {
        userRepo.save(testUser);

        mockMvc.perform(put("/users/12345678901/situacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao", is(false)));
    }

    @Test
    @DisplayName("DELETE /users/{cpf} should delete user")
    void testDeleteUser() throws Exception {
        userRepo.save(testUser);

        mockMvc.perform(delete("/users/12345678901"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/{cpf} should return 404 when user not found")
    void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/users/00000000000"))
                .andExpect(status().isNotFound());
    }
}
