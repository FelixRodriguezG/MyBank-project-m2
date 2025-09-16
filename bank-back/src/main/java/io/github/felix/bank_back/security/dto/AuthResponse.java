package io.github.felix.bank_back.security.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private long expiresIn; // segundos

    public AuthResponse(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
    public String getToken() { return token; }
    public long getExpiresIn() { return expiresIn; }
}
