package br.anhembi.spring_proja3.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {

    private final Map<String, TokenInfo> tokens = new HashMap<>();

    private static final int TOKEN_EXPIRATION_MINUTES = 120; 

    public String generateToken(String userId) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);

        tokens.put(token, new TokenInfo(userId, expirationTime));
        return token;
    }

    public boolean isTokenValid(String token) {
        TokenInfo tokenInfo = tokens.get(token);

        if (tokenInfo == null) {
            return false; 
        }

        if (tokenInfo.getExpirationTime().isBefore(LocalDateTime.now())) {
            tokens.remove(token);
            return false; 
        }

        return true;
    }

    public String getUserCPFfromToken(String token) {
        TokenInfo tokenInfo = tokens.get(token);
        return tokenInfo != null ? tokenInfo.getUserId() : null;
    }

    public boolean invalidateToken(String token) {

        if (!isTokenValid(token)) {
            return false; 
        }

        tokens.remove(token);
        return true;
    }

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

