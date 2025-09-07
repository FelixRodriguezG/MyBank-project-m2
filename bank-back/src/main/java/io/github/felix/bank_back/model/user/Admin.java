package io.github.felix.bank_back.model.user;

import io.github.felix.bank_back.model.user.enums.UserStatus;
import io.github.felix.bank_back.model.user.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern.List({
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter"),
            @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter"),
            @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit"),
            @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must contain at least one special character (!@#$%^&*())")
    })
    @Column(nullable = false)
    private String password;

    @NotNull
    private Role role;

    @NotNull
    private UserStatus status;

    /* CONSTRUCTOR */
    public Admin(String name, String password) {
        this.name = name;
        this.password = password;
        this.role = Role.ADMIN;
        this.status = UserStatus.ACTIVE;
    }

}


