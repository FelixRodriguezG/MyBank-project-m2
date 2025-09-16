package io.github.felix.bank_back.model.transaction;


import io.github.felix.bank_back.model.account.Account;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.transaction.enums.TransactionStatus;
import io.github.felix.bank_back.model.transaction.enums.TransactionType;
import io.github.felix.bank_back.model.user.ThirdParty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, name = "transaction_date")
    private LocalDateTime transactionDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "transaction_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "transaction_currency", length = 3, nullable = false))
    })
    @NotNull
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(length = 500)
    private String description;

    // Relación con la cuenta involucrada
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Relación opcional con ThirdParty (solo si es transacción de tercero)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "third_party_id")
    private ThirdParty thirdParty;

    // Para transferencias entre cuentas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;


    // Constructor para transacciones de terceros
    public Transaction(Money amount, TransactionType type, Account account, ThirdParty thirdParty, String description) {
        this.amount = amount;
        this.type = type;
        this.account = account;
        this.thirdParty = thirdParty;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
        this.status = TransactionStatus.COMPLETED;
    }

    // Constructor para transferencias entre cuentas
    public Transaction(Money amount, Account sourceAccount, Account targetAccount, String description) {
        this.amount = amount;
        this.type = TransactionType.TRANSFER;
        this.account = sourceAccount;
        this.targetAccount = targetAccount;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
        this.status = TransactionStatus.COMPLETED;
    }

    // Constructor para operaciones internas (depósito, retiro, intereses, etc.)
    public Transaction(Money amount, TransactionType type, Account account, String description) {
        this.amount = amount;
        this.type = type;
        this.account = account;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
        this.status = TransactionStatus.COMPLETED;
    }

    // Métodos de utilidad
    public boolean isThirdPartyTransaction() {
        return thirdParty != null;
    }

    public boolean isTransfer() {
        return type == TransactionType.TRANSFER && targetAccount != null;
    }

    public boolean isSuccessful() {
        return status == TransactionStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return String.format("Transaction [ID: %d, Type: %s, Amount: %s, Account: %d, Date: %s, Status: %s]",
                id, type, amount, account.getId(), transactionDate, status);
    }
}

