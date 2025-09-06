package io.github.felix.bank_back.model.user;

import io.github.felix.bank_back.model.user.enums.UserStatus;
import io.github.felix.bank_back.model.user.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class SystemUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    private Role role;

    @NotNull
    private UserStatus status;

    public SystemUser(String name, Role role, UserStatus status) {
        this.name = name;
        this.role = role;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

}
