package br.anhembi.groove_aplication.controller;

import java.util.List;
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

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<User> getUserByCpf(@PathVariable String cpf) {
        return userService.getUser(cpf)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(cpf));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.insert(user));
    }

    @PostMapping("/VIP")
    public ResponseEntity<User> insertUserVIP(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.insertVIP(user));
    }

    @PutMapping("/{cpf}/prim-reserva")
    public ResponseEntity<User> updatePrimReserva(@PathVariable String cpf, @RequestBody String primReserva) {
        return ResponseEntity.ok(userService.updatePrimReserva(cpf, primReserva));
    }

    @PutMapping("/{cpf}/seg-reserva")
    public ResponseEntity<User> updateSegReserva(@PathVariable String cpf, @RequestBody String segReserva) {
        return ResponseEntity.ok(userService.updateSegReserva(cpf, segReserva));
    }

    @PutMapping("/{cpf}/situacao")
    public ResponseEntity<User> updateSituacao(@PathVariable String cpf, @RequestBody boolean situacao) {
        return ResponseEntity.ok(userService.updateSituacao(cpf, situacao));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteUser(@PathVariable String cpf) {
        userService.delete(cpf);
        return ResponseEntity.noContent().build();
    }
}
