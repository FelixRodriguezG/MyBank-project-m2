package io.github.felix.bank_back.model.user;

import io.github.felix.bank_back.model.user.enums.UserStatus;
import io.github.felix.bank_back.model.user.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Data
@NoArgsConstructor
@Table(name = "admins")
public class Admin  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(nullable = false, unique = true)
    private String username;



    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern.List({
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter"),
            @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter"),
            @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit"),
            @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must contain at least one special character (!@#$%^&*())")
    })

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(nullable = false, length = 60)
    private String password;

    @Transient
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Metodo para encriptar y setear la contraseña
    public void setPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        this.password = passwordEncoder.encode(plainPassword);
    }
    // Metodo para verificar la contraseña ingresada con la almacenada
    public boolean verifyPassword(String plainPassword) {
        if (plainPassword == null || this.password == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, this.password);
    }

    /* CONSTRUCTOR */
    public Admin(String name, String password) {
        this.name = name;
        setPassword(password);
        this.role = Role.ADMIN;
        this.status = UserStatus.ACTIVE;
    }

}


