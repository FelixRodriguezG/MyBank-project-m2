package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**** balance: Money embebido ****/
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",       column = @Column(name = "balance_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "balance_currency", length = 3, nullable = false))
    })
    @NotNull
    private Money balance;

    @NotNull
    @Column(nullable = false)
    private String secretKey;

    @NotNull
    @Column(nullable = false)
    private LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",       column = @Column(name = "penaltyFee_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "penaltyFee_currency", length = 3, nullable = false))
    })
    @NotNull
    private Money penaltyFee = new Money(new BigDecimal("40"));

    /* Relaciones con AccountHolder (Propietarios de la cuenta)
    * * -> Primary Owner y Secondary Owner son obligatorios.
    * * -> La relación es ManyToOne porque un AccountHolder puede tener muchas cuentas.
    * * -> La relación es EAGER porque siempre que se consulte una cuenta, se querrá saber quiénes son sus propietarios.
    */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "primary_owner_id", nullable = false)
    private AccountHolder primaryOwner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "secondary_owner_id")
    private AccountHolder secondaryOwner;


    // CONSTRUCTORES
    // Constructor para un propietario principal
    public Account(Money balance, String secretKey, AccountHolder primaryOwner) {
        this.balance = balance;
        this.secretKey = secretKey;
        this.primaryOwner = primaryOwner;
        this.creationDate = LocalDate.now();
        this.status = AccountStatus.ACTIVE;
        this.penaltyFee = new Money(new BigDecimal("40"));
    }

    // Constructor para cuenta con dos propietarios
    public Account(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        this.balance = balance;
        this.secretKey = secretKey;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.creationDate = LocalDate.now();
        this.status = AccountStatus.ACTIVE;
        this.penaltyFee = new Money(new BigDecimal("40"));
    }


}
