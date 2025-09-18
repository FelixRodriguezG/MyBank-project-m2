package io.github.felix.bank_back.controller.auth;

import io.github.felix.bank_back.security.JwtService;
import io.github.felix.bank_back.security.dto.AuthRequest;
import io.github.felix.bank_back.security.dto.AuthResponse;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticación y emisión de tokens JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Login", description = "Autentica credenciales y devuelve un JWT válido por 1 hora")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user.getUsername());
        long expiresInSeconds = 3600L; // 1 hora
        return ResponseEntity.ok(new AuthResponse(token, expiresInSeconds));
    }
}
