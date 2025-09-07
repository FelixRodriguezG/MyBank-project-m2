package io.github.felix.bank_back.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil  {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Hashea una contraseña usando BCrypt
     * @param password Contraseña en texto plano
     * @return Hash BCrypt de la contraseña
     */
    public String hashPassword(String password) {
        if(password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(password);
    }

    /**
     * Verifica si una contraseña coincide con un hash
     * @param password Contraseña en texto plano
     * @param hashedPassword Hash almacenado
     * @return true si coinciden
     */
    public boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(password, hashedPassword);
    }

    /**
     * Obtiene la instancia del encoder (útil para Spring Security)
     * @return BCryptPasswordEncoder
     */
    public static BCryptPasswordEncoder getEncoder() {
        return passwordEncoder;
    }
}
