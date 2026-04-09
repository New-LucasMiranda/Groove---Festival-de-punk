package br.anhembi.groove_aplication.controller;

import br.anhembi.groove_aplication.entities.Sectors;
import br.anhembi.groove_aplication.entities.Ticket;
import br.anhembi.groove_aplication.service.SectorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sectors")
@CrossOrigin("*")
public class SectorsController {

    @Autowired
    private SectorsService service;

    // Stack a ticket into a sector
    @PostMapping("/{nomeSetor}/stack")
    public ResponseEntity<String> stackTicket(@PathVariable String nomeSetor, @RequestBody Ticket ticket) {
        boolean success = service.stackTicket(nomeSetor, ticket);
        if (success) {
            return ResponseEntity.ok("Ingresso empilhado com sucesso no setor " + nomeSetor);
        }
        return ResponseEntity.status(400).body("Falha ao empilhar ingresso no setor " + nomeSetor);
    }

    // Unstack a ticket from a sector
    @PostMapping("/{nomeSetor}/unstack")
    public ResponseEntity<Ticket> unstackTicket(@PathVariable String nomeSetor) {
        Ticket ticket = service.unstack(nomeSetor);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        }
        return ResponseEntity.status(404).build();
    }

    // Get occupation rate of a sector
    @GetMapping("/{nomeSetor}/occupation-rate")
    public ResponseEntity<Double> getOccupationRate(@PathVariable String nomeSetor) {
        return ResponseEntity.ok(service.getOccupationRate(nomeSetor));
    }

    // Get available ticket quantity for a sector name (summed across days)
    @GetMapping("/{nomeSetor}/quantity")
    public ResponseEntity<Integer> getQtdDisp(@PathVariable String nomeSetor) {
        return ResponseEntity.ok(service.getQtdDisp(nomeSetor));
    }

    // Get all sectors
    @GetMapping
    public ResponseEntity<?> getAllSectors() {
        return ResponseEntity.ok(service.getAllSectors());
    }

    // Create a new sector
    @PostMapping
    public ResponseEntity<String> createSector(@RequestBody Sectors sector) {
        Sectors created = service.insert(sector);
        return ResponseEntity.ok("Setor " + created.getNome() + " criado com sucesso.");
    }

    // Delete a sector by nome + dia (composite key)
    @DeleteMapping("/{nomeSetor}/{dia}")
    public ResponseEntity<String> deleteSector(@PathVariable String nomeSetor, @PathVariable String dia) {
        boolean success = service.delete(nomeSetor, dia);
        if (success) {
            return ResponseEntity.ok("Setor " + nomeSetor + " excluído com sucesso.");
        }
        return ResponseEntity.status(404).body("Setor " + nomeSetor + " não encontrado.");
    }

    // Decrement available quantity for a sector by nome + dia
    @PatchMapping("/{nomeSetor}/{dia}/decrement")
    public ResponseEntity<String> decrementQtdDisp(@PathVariable String nomeSetor, @PathVariable String dia) {
        Sectors updated = service.decrementQtdDisp(nomeSetor, dia);
        if (updated != null) {
            return ResponseEntity.ok("Quantidade disponível de " + nomeSetor + " atualizada para " + updated.getQtdDisp());
        }
        return ResponseEntity.status(400).body("Falha ao atualizar a quantidade de " + nomeSetor);
    }
}
