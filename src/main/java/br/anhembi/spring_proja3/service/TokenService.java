package br.anhembi.spring_proja3.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {

    // Armazena tokens e suas informações associadas
    private final Map<String, TokenInfo> tokens = new HashMap<>();

    private static final int TOKEN_EXPIRATION_MINUTES = 120; // Tempo de validade do token

    /**
     * Gera um token único e o associa com um identificador, como o CPF do usuário.
     *
     * @param userId Identificador único do usuário.
     * @return O token gerado.
     */
    public String generateToken(String userId) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);

        tokens.put(token, new TokenInfo(userId, expirationTime));
        return token;
    }

    /**
     * Valida se um token é válido e não expirou.
     *
     * @param token O token a ser validado.
     * @return Verdadeiro se o token for válido; falso caso contrário.
     */
    public boolean isTokenValid(String token) {
        TokenInfo tokenInfo = tokens.get(token);

        if (tokenInfo == null) {
            return false; // Token não encontrado
        }

        if (tokenInfo.getExpirationTime().isBefore(LocalDateTime.now())) {
            tokens.remove(token); // Remove o token expirado
            return false; // Token expirado
        }

        return true;
    }

    /**
     * Obtém o identificador associado ao token.
     *
     * @param token O token.
     * @return O identificador do usuário associado ao token, ou null se o token for inválido.
     */
    public String getUserCPFfromToken(String token) {
        TokenInfo tokenInfo = tokens.get(token);
        return tokenInfo != null ? tokenInfo.getUserId() : null;
    }

    /**
     * Método para invalidar um token explicitamente.
     *
     * @param token O token a ser invalidado.
     * @return true se o token foi invalidado com sucesso, false se o token já estava inválido ou expirado.
     */
    public boolean invalidateToken(String token) {
        // Verifica se o token é válido utilizando a função isTokenValid
        if (!isTokenValid(token)) {
            return false; // O token já está inválido ou expirado
        }

        tokens.remove(token); // Remove o token do mapa, tornando-o inválido
        return true; // Token invalidado com sucesso
    }

    // Classe interna para armazenar informações sobre o token
    private static class TokenInfo {
        private final String userId;
        private final LocalDateTime expirationTime;

        public TokenInfo(String userId, LocalDateTime expirationTime) {
            this.userId = userId;
            this.expirationTime = expirationTime;
        }

        public String getUserId() {
            return userId;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }
    }
}

