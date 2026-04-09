package br.anhembi.groove_aplication.controller;

import br.anhembi.groove_aplication.service.TokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateToken(@RequestParam String userId) {
        try {
            return ResponseEntity.ok(tokenService.generateToken(userId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao gerar o token.");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        if (tokenService.isTokenValid(token)) {
            return ResponseEntity.ok("Token válido.");
        }
        return ResponseEntity.status(400).body("Token inválido ou expirado.");
    }

    @PostMapping("/invalidate")
    public ResponseEntity<String> invalidateToken(@RequestParam String token) {
        boolean wasInvalidated = tokenService.invalidateToken(token);
        if (wasInvalidated) {
            return ResponseEntity.ok("Token invalidado com sucesso.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token já inválido ou expirado.");
    }

    @GetMapping("/get-user-id")
    public ResponseEntity<?> getUserCPFfromToken(@RequestParam String token) {
        if (!tokenService.isTokenValid(token)) {
            return ResponseEntity.status(400).body("Token inválido ou expirado.");
        }
        String userId = tokenService.getUserCPFfromToken(token);
        if (userId != null) {
            return ResponseEntity.ok(java.util.Map.of("cpf", userId));
        }
        return ResponseEntity.status(400).body("Token inválido.");
    }
}
