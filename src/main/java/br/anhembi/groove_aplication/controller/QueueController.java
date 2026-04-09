package br.anhembi.groove_aplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.anhembi.groove_aplication.Structures.UserQueue;
import br.anhembi.groove_aplication.entities.User;
import br.anhembi.groove_aplication.service.QueueService;

@RestController
@RequestMapping("/queues")
@CrossOrigin("*")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @GetMapping
    public ResponseEntity<List<UserQueue>> getAllQueues() {
        return ResponseEntity.ok(queueService.getAllQueues());
    }

    @GetMapping("/{dia}")
    public ResponseEntity<UserQueue> getQueueByDia(@PathVariable String dia) {
        return ResponseEntity.ok(queueService.getQueueByDia(dia));
    }

    @GetMapping("/{dia}/position/{cpf}")
    public ResponseEntity<?> getUserPositionInQueue(@PathVariable String dia, @PathVariable String cpf) {
        String normalizedCpf = cpf.replaceAll("[^\\d]", "");
        int position = queueService.getUserPositionByCpf(normalizedCpf, dia);
        if (position == -1) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(position);
    }

    @GetMapping("/{dia}/occupation-rate")
    public ResponseEntity<Double> getOccupationRate(@PathVariable String dia) {
        return ResponseEntity.ok(queueService.getOccupationRate(dia));
    }

    @PostMapping("/enqueue")
    public ResponseEntity<?> enqueueUser(@RequestBody User user) {
        try {
            queueService.enqueueUser(user);
            return ResponseEntity.ok(user);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao adicionar o usuário: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao processar a requisição.");
        }
    }

    @DeleteMapping("/{dia}/dequeue")
    public ResponseEntity<User> dequeueUser(@PathVariable String dia) {
        User dequeuedUser = queueService.dequeueUser(dia);
        if (dequeuedUser != null) {
            return ResponseEntity.ok(dequeuedUser);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{dia}/remove/{cpf}")
    public ResponseEntity<Boolean> removeUserFromQueue(@PathVariable String dia, @PathVariable String cpf) {
        String normalizedCpf = cpf.replaceAll("[^\\d]", "");
        return ResponseEntity.ok(queueService.removeUserByCpf(normalizedCpf, dia));
    }

    @GetMapping("/peek/{dia}")
    public ResponseEntity<User> peek(@PathVariable String dia) {
        User user = queueService.peek(dia);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
