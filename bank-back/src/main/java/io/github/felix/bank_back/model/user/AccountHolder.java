package io.github.felix.bank_back.model.user;

import io.github.felix.bank_back.model.user.embedded.Address;
import io.github.felix.bank_back.model.user.embedded.PersonalData;
import io.github.felix.bank_back.model.user.enums.Role;
import io.github.felix.bank_back.model.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@Data
@NoArgsConstructor
@Table(name = "account_holders")
public class AccountHolder  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank
    @Column(nullable = false, length = 60) // BCrypt produce hashes de 60 caracteres
    private String password;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @NotNull
    @Embedded
    private PersonalData personalData;

    @NotNull
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "primary_street")),
            @AttributeOverride(name = "city", column = @Column(name = "primary_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "primary_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "primary_country"))
    })
    private Address primaryAddress;

    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "mailingAddress_street")),
            @AttributeOverride(name = "city", column = @Column(name = "mailingAddress_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "mailingAddress_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "mailingAddress_country"))
    })
    private Address mailingAddress;

    public int getAge() {
        LocalDate birthDate = LocalDate.parse(this.personalData.getDateOfBirth());
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public boolean isEligibleForStudentAccount() {
        int age = getAge();
        if(age < 18) {
            throw new IllegalStateException("El titular de la cuenta debe ser mayor de edad para abrir una cuenta de estudiante.");
        }
        return age < 24;
    }

    public AccountHolder(String name,String password, PersonalData personalData, Address primaryAddress,
                         Address mailingAddress ,
                         Role role, UserStatus status) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.status = status;
        this.personalData= personalData;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = (mailingAddress != null) ? mailingAddress : primaryAddress;
    }
    public AccountHolder(String name,String password, PersonalData personalData, Address primaryAddress,
                         Role role, UserStatus status) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.status = status;
        this.personalData= personalData;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = primaryAddress;
    }

    // @Transient para que JPA no intente mapear este campo
    // Encoder necesario para hashear las contraseñas
    @Transient
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

   // Hashea y establece la contraseña del usuario,
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        this.password = passwordEncoder.encode(password);
    }
    // Verifica si la contraseña en texto plano coincide con la almacenada (hasheada)
    public boolean verifyPassword(String plainPassword) {
        if (plainPassword == null || this.password == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, this.password);
    }
}