package br.anhembi.spring_proja3.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.anhembi.spring_proja3.entities.User;
import br.anhembi.spring_proja3.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    // Obter todos os usuários
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Obter um usuário por CPF
    @GetMapping("/{cpf}")
    public ResponseEntity<User> getUserByCpf(@PathVariable String cpf) {
        return userService.getUser(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar um novo usuário
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.insert(user);
        if (createdUser == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/VIP")
    public ResponseEntity<User> insertUserVIP(@RequestBody User user) {
        User savedUser = userService.insertVIP(user);

        if (savedUser == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(savedUser);
    }

    // Atualizar a primeira reserva de um usuário
    @PutMapping("/{cpf}/prim-reserva")
    public ResponseEntity<User> updatePrimReserva(@PathVariable String cpf, @RequestBody String primReserva) {
        User updatedUser = userService.updatePrimReserva(cpf, primReserva);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    // Atualizar a segunda reserva de um usuário
    @PutMapping("/{cpf}/seg-reserva")
    public ResponseEntity<User> updateSegReserva(@PathVariable String cpf, @RequestBody String segReserva) {
        User updatedUser = userService.updateSegReserva(cpf, segReserva);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    // Atualizar a situação de um usuário
    @PutMapping("/{cpf}/situacao")
    public ResponseEntity<User> updateSituacao(@PathVariable String cpf, @RequestBody boolean situacao) {
        User updatedUser = userService.updateSituacao(cpf, situacao);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    // Deletar um usuário por CPF
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteUser(@PathVariable String cpf) {
        if (userService.delete(cpf)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
