package io.github.felix.bank_back.model.user;

import io.github.felix.bank_back.model.user.embedded.Address;
import io.github.felix.bank_back.model.user.embedded.PersonalData;
import io.github.felix.bank_back.model.user.enums.Role;
import io.github.felix.bank_back.model.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    private Role role;

    @NotNull
    private UserStatus status;

    @NotNull
    @Embedded
    private PersonalData personalData;

    @NotNull
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "primary_street")),
            @AttributeOverride(name = "city", column = @Column(name = "primary_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "primary_zip_code")),
            @AttributeOverride(name = "country", column = @Column(name = "primary_country"))
    })
    private Address primaryAddress;

    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "mailingAddress_street")),
            @AttributeOverride(name = "city", column = @Column(name = "mailingAddress_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "mailingAddress_zip_code")),
            @AttributeOverride(name = "country", column = @Column(name = "mailingAddress_country"))
    })
    private Address mailingAddress;

    public int getAge() {
        LocalDate birthDate = LocalDate.parse(this.personalData.getDateOfBirth());
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public boolean isEligibleForStudentAccount() {
        return getAge() < 24;
    }

    public AccountHolder(String name, PersonalData personalData, Address primaryAddress, Address mailingAddress ,
                         Role role, UserStatus status) {
        this.name = name;
        this.role = role;
        this.status = status;
        this.personalData= personalData;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = (mailingAddress != null) ? mailingAddress : primaryAddress;
    }
    public AccountHolder(String name, PersonalData personalData, Address primaryAddress,
                         Role role, UserStatus status) {
        this.name = name;
        this.role = role;
        this.status = status;
        this.personalData= personalData;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = primaryAddress;
    }
}