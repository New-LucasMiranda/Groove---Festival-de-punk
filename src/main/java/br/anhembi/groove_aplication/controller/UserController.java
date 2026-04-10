package br.anhembi.groove_aplication.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import br.anhembi.groove_aplication.entities.User;
import br.anhembi.groove_aplication.exception.UserNotFoundException;
import br.anhembi.groove_aplication.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<User> getUserByCpf(@PathVariable String cpf) {
        String normalizedCpf = cpf.replaceAll("[^\\d]", "");
        return userService.getUser(normalizedCpf)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(normalizedCpf));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        logger.info("Received request to create user: {}", user);
        User createdUser = userService.insert(user);
        logger.info("User created successfully: {}", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/VIP")
    public ResponseEntity<User> insertUserVIP(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.insertVIP(user));
    }

    @PutMapping("/{cpf}/prim-reserva")
    public ResponseEntity<User> updatePrimReserva(@PathVariable String cpf, @RequestBody String primReserva) {
        String normalizedCpf = cpf.replaceAll("[^\\d]", "");
        return ResponseEntity.ok(userService.updatePrimReserva(normalizedCpf, primReserva));
    }

    @PutMapping("/{cpf}/seg-reserva")
    public ResponseEntity<User> updateSegReserva(@PathVariable String cpf, @RequestBody String segReserva) {
        String normalizedCpf = cpf.replaceAll("[^\\d]", "");
        return ResponseEntity.ok(userService.updateSegReserva(normalizedCpf, segReserva));
    }

    @PutMapping("/{cpf}/situacao")
    public ResponseEntity<User> updateSituacao(@PathVariable String cpf, @RequestBody boolean situacao) {
        String normalizedCpf = cpf.replaceAll("[^\\d]", "");
        return ResponseEntity.ok(userService.updateSituacao(normalizedCpf, situacao));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteUser(@PathVariable String cpf) {
        String normalizedCpf = cpf.replaceAll("[^\\d]", "");
        userService.delete(normalizedCpf);
        return ResponseEntity.noContent().build();
    }
}
