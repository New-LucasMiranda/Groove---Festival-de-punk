package br.anhembi.spring_proja3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.anhembi.spring_proja3.Structures.UserQueue;
import br.anhembi.spring_proja3.entities.User;
import br.anhembi.spring_proja3.service.QueueService;

@RestController
@RequestMapping("/queues")
@CrossOrigin("*")
public class QueueController {

     @Autowired
     private QueueService queueService;

     @GetMapping()
     public ResponseEntity<List<UserQueue>> getAllQueues() {
          List<UserQueue> queues = queueService.getAllQueues();
          return ResponseEntity.ok(queues); // Retorna todas as filas disponíveis
     }

     @GetMapping("/{dia}")
     public ResponseEntity<UserQueue> getQueueByDia(@PathVariable String dia) {
          UserQueue queue = queueService.getQueueByDia(dia);
          return ResponseEntity.ok(queue);
     }

     // Obter a posição de um usuário na fila por CPF e dia
     @GetMapping("/{dia}/position/{cpf}")
     public ResponseEntity<Integer> getUserPositionInQueue(@PathVariable String dia, @PathVariable String cpf) {
          int position = queueService.getUserPositionByCpf(cpf, dia);
          return ResponseEntity.ok(position);
     }

     // Obter a taxa de ocupação de uma fila por dia
     @GetMapping("/{dia}/occupation-rate")
     public ResponseEntity<Double> getOccupationRate(@PathVariable String dia) {
          double occupationRate = queueService.getOccupationRate(dia);
          return ResponseEntity.ok(occupationRate);
     }

      @PostMapping("/enqueue")
      public ResponseEntity<?> enqueueUser(@RequestBody User user) {
          try {
              // Chama o método não estático no serviço
              queueService.enqueueUser(user);
              return ResponseEntity.ok(user); // Retorna o objeto User adicionado.
          } catch (IllegalStateException e) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                      .body("Erro ao adicionar o usuário: " + e.getMessage());
          } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                      .body("Erro interno ao processar a requisição.");
          }
      }
     // Remover o primeiro usuário de uma fila por dia
     @DeleteMapping("/{dia}/dequeue")
     public ResponseEntity<User> dequeueUser(@PathVariable String dia) {
          User dequeuedUser = queueService.dequeueUser(dia);
          if (dequeuedUser != null) {
               return ResponseEntity.ok(dequeuedUser);
          }
          return ResponseEntity.noContent().build();
     }

     // Remover um usuário de uma fila por CPF e dia
     @DeleteMapping("/{dia}/remove/{cpf}")
     public ResponseEntity<Boolean> removeUserFromQueue(@PathVariable String dia, @PathVariable String cpf) {
          boolean removed = queueService.removeUserByCpf(cpf, dia);
          return ResponseEntity.ok(removed);
     }
     @GetMapping("/peek/{dia}")
     public ResponseEntity<User> peek(@PathVariable String dia) {
         User user = queueService.peek(dia); // Chama o método do serviço
         if (user != null) {
             return ResponseEntity.ok(user); // Se o usuário for encontrado, retorna OK
         }
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retorna 404 se não encontrar
     }
}
