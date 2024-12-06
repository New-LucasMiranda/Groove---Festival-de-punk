package br.anhembi.spring_proja3.controller;

import br.anhembi.spring_proja3.service.TokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final TokenService tokenService;

    // Injeção de dependência do TokenService
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Endpoint para gerar um novo token para um usuário.
     * 
     * @param userId O ID do usuário (por exemplo, CPF).
     * @return O token gerado.
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateToken(@RequestParam String userId) {
        try {
            String token = tokenService.generateToken(userId);
            return ResponseEntity.ok(token);  // Retorna o token gerado
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao gerar o token.");
        }
    }

    /**
     * Endpoint para validar um token.
     * 
     * @param token O token a ser validado.
     * @return Status de validade do token.
     */
    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        if (tokenService.isTokenValid(token)) {
            return ResponseEntity.ok("Token válido.");
        } else {
            return ResponseEntity.status(400).body("Token inválido ou expirado.");
        }
    }

    /**
     * Endpoint para invalidar um token explicitamente.
     * 
     * @param token O token a ser invalidado.
     * @return Confirmação de invalidação.
     */
   @PostMapping("/invalidate")
    public ResponseEntity<String> invalidateToken(@RequestParam String token) {
        // Tenta invalidar o token
        boolean wasInvalidated = tokenService.invalidateToken(token);

        if (wasInvalidated) {
            return ResponseEntity.ok("Token invalidado com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token já inválido ou expirado.");
        }
    }

    /**
     * Endpoint para obter o userId associado a um token.
     * 
     * @param token O token.
     * @return O userId associado ao token, ou uma mensagem de erro se o token for inválido.
     */
    @GetMapping("/get-user-id")
    public ResponseEntity<String> getUserCPFfromToken(@RequestParam String token) {
     // Valida o token
     if (!tokenService.isTokenValid(token)) {
         // Se o token for inválido ou expirado, retorna uma mensagem de erro
         return ResponseEntity.status(400).body("Token inválido ou expirado.");
     }

     // Se o token for válido, obtém o userId associado
     String userId = tokenService.getUserCPFfromToken(token);
     if (userId != null) {
         return ResponseEntity.ok(userId); // Retorna o userId
     } else {
         return ResponseEntity.status(400).body("Token inválido.");
     }
 }
}
