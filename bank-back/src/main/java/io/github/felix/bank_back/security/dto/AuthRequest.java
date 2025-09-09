package io.github.felix.bank_back.security.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
