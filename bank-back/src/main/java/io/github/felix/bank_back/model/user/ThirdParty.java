package io.github.felix.bank_back.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "third_parties")
public class ThirdParty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String hashedKey;

    @Transient
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Setea la key hasheada
    public void setPassword(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        this.hashedKey = passwordEncoder.encode(key);
    }

    // Verifica si la contraseña ingresada coincide con la almacenada
    public boolean verifyPassword(String key) {
        if (key == null || this.hashedKey == null) {
            return false;
        }
        return passwordEncoder.matches(key, this.hashedKey);
    }

    public ThirdParty(String name, String key) {
        this.name = name;
        setPassword(key);
    }

}
