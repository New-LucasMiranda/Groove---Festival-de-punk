package br.anhembi.spring_proja3.controller;

import br.anhembi.spring_proja3.entities.Sectors;
import br.anhembi.spring_proja3.entities.Ticket;
import br.anhembi.spring_proja3.service.SectorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sectors")
@CrossOrigin("*")
public class SectorsController {

    @Autowired
    private SectorsService service;

    // Endpoint para empilhar um ingresso em um setor específico
    @PostMapping("/{nomeSetor}/stack")
    public ResponseEntity<String> stackTicket(@PathVariable String nomeSetor, @RequestBody Ticket ticket) {
        boolean success = service.stackTicket(nomeSetor, ticket);
        if (success) {
            return ResponseEntity.ok("Ingresso empilhado com sucesso no setor " + nomeSetor);
        } else {
            return ResponseEntity.status(400).body("Falha ao empilhar ingresso no setor " + nomeSetor);
        }
    }

    // Endpoint para desempilhar um ingresso de um setor específico
    @PostMapping("/{nomeSetor}/unstack")
    public ResponseEntity<Ticket> unstackTicket(@PathVariable String nomeSetor) {
        Ticket ticket = service.unstack(nomeSetor);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        } else {
            return ResponseEntity.status(404).body(null); // Retorna 404 se não houver ingressos na pilha
        }
    }

    // Endpoint para obter a taxa de ocupação de um setor
    @GetMapping("/{nomeSetor}/occupation-rate")
    public ResponseEntity<Double> getOccupationRate(@PathVariable String nomeSetor) {
        double occupationRate = service.getOccupationRate(nomeSetor);
        return ResponseEntity.ok(occupationRate);
    }

    // Endpoint para obter a quantidade disponível de ingressos de um setor
    @GetMapping("/{nomeSetor}/quantity")
    public ResponseEntity<Integer> getQtdDisp(@PathVariable String nomeSetor) {
        int qtdDisp = service.getQtdDisp(nomeSetor);
        return ResponseEntity.ok(qtdDisp);
    }

    // Endpoint para obter a lista de todos os setores
    @GetMapping
    public ResponseEntity<?> getAllSectors() {
        return ResponseEntity.ok(service.getAllSectors());
    }

    // Endpoint para criar um novo setor
    @PostMapping
    public ResponseEntity<String> createSector(@RequestBody Sectors sector) {
        Sectors createdSector = service.insert(sector);
        return ResponseEntity.ok("Setor " + createdSector.getNome() + " criado com sucesso.");
    }

    // Endpoint para excluir um setor
    @DeleteMapping("/{nomeSetor}")
    public ResponseEntity<String> deleteSector(@PathVariable String nomeSetor) {
        boolean success = service.delete(nomeSetor);
        if (success) {
            return ResponseEntity.ok("Setor " + nomeSetor + " excluído com sucesso.");
        } else {
            return ResponseEntity.status(404).body("Setor " + nomeSetor + " não encontrado.");
        }
    }

    // Endpoint para atualizar a quantidade disponível de ingressos de um setor
    @PatchMapping("/{nomeSetor}/decrement")
    public ResponseEntity<String> decrementQtdDisp(@PathVariable String nomeSetor) {
        Sectors updatedSector = service.decrementQtdDisp(nomeSetor);
        if (updatedSector != null) {
            return ResponseEntity.ok("Quantidade disponível de " + nomeSetor + " atualizada para " + updatedSector.getQtdDisp());
        } else {
            return ResponseEntity.status(400).body("Falha ao atualizar a quantidade de " + nomeSetor);
        }
    }
}
