package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class StudentChecking extends Account{

    // ==================== STUDENT_CHECKING_INFO ====================
    // Hereda de Account:id, balance, secretKey, primaryOwner, secondaryOwner, creationDate, status, accountType.
    // Atributos específicos de StudentChecking: Ninguno
    // Comportamiento específico: Validación de edad del titular, sin saldo mínimo requerido, sin comisión mensual.


    // * Constructor con AccountHolder(propietario principal)
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner, AccountType.STUDENT_CHECKING);
        validateStudentEligibility(primaryOwner);
    }

    // * Constructor con AccountHolder(propietario principal) y AccountHolder(propietario secundario)
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, secretKey, primaryOwner, secondaryOwner, AccountType.STUDENT_CHECKING);
        validateStudentEligibility(primaryOwner);
        if (secondaryOwner != null) {
            validateStudentEligibility(secondaryOwner);
        }
    }

    // ==================== VALIDACIÓN DE EDAD ====================

    // * Método privado para validar la elegibilidad del titular de la cuenta
    private void validateStudentEligibility(AccountHolder accountHolder) {
        if (!accountHolder.isEligibleForStudentAccount()) {
            throw new IllegalArgumentException(
                    String.format("Account holder '%s' is not eligible for student account (age: %d). Must be under 24.",
                            accountHolder.getName(), accountHolder.getAge())
            );
        }
    }
    // MOVER AL SERVICE Withdraw, deposit, transferTo, receiveTransfer y validateTransaction a un servicio
    // ==================== OPERACIONES BANCARIAS ====================

    // * Las cuentas estudiantiles no tienen restricciones de saldo mínimo
    // * Solo verifica que no se retire más de lo disponible
    public boolean canWithdraw(Money amount) {
        return getBalance().getAmount().compareTo(amount.getAmount()) >= 0;
    }
    // * Para transferencias, se aplican las mismas reglas que para retiros
    public boolean canTransfer(Money amount) {
        if (amount == null || amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return canWithdraw(amount);
    }
    // * Método para retirar dinero de la cuenta  -------(SERVICE)
    public boolean withdraw(Money amount) {
        if (!canWithdraw(amount)) {
            return false;
        }

        validateTransaction(amount, "WITHDRAWAL");
        getBalance().decreaseAmount(amount);
        return true;
    }

    // * Método para depositar dinero en la cuenta -------(SERVICE)
    public void deposit(Money amount) {
        if (amount == null || amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        if (!amount.getCurrencyCode().equals(getBalance().getCurrencyCode())) {
            throw new IllegalArgumentException("Currency mismatch in deposit");
        }

        validateTransaction(amount, "DEPOSIT");
        getBalance().increaseAmount(amount);
    }
    // * Método para transferir dinero a otra cuenta -------(SERVICE)
    public boolean transferTo(Money amount, Account targetAccount) {
        if (!canTransfer(amount)) {
            return false;
        }

        if (targetAccount == null) {
            throw new IllegalArgumentException("Target account cannot be null");
        }

        if (!amount.getCurrencyCode().equals(targetAccount.getBalance().getCurrencyCode())) {
            throw new IllegalArgumentException("Currency mismatch between accounts");
        }

        // Validar transacción antes de ejecutar
        validateTransaction(amount, "TRANSFER_OUT");

        // Ejecutar transferencia
        getBalance().decreaseAmount(amount);
        targetAccount.getBalance().increaseAmount(amount);

        return true;
    }
    // * Método para recibir una transferencia de otra cuenta ------(SERVICE)
    public void receiveTransfer(Money amount, Account sourceAccount) {
        if (amount == null || amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (!amount.getCurrencyCode().equals(getBalance().getCurrencyCode())) {
            throw new IllegalArgumentException("Currency mismatch in transfer");
        }

        validateTransaction(amount, "TRANSFER_IN");
        getBalance().increaseAmount(amount);
    }

    // ==================== VALIDACIONES ====================
    // * Método para validar una transacción antes de ejecutarla -------(SERVICE)
    public void validateTransaction(Money amount, String operationType) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Verificar que la cuenta esté activa
        if (getStatus() != io.github.felix.bank_back.model.account.enums.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot perform transactions on inactive account");
        }

        // Para operaciones que requieren fondos
        if ("WITHDRAWAL".equals(operationType) || "TRANSFER_OUT".equals(operationType)) {
            if (!canWithdraw(amount)) {
                throw new IllegalArgumentException("Insufficient funds for " + operationType.toLowerCase());
            }
        }
    }

    // * Método para verificar si hay suficiente saldo para una operación
    public boolean hasEnoughBalance(Money amount) {
        return canWithdraw(amount);
    }

    // ==================== INFORMACIÓN Y UTILIDADES ====================

    @Override
    public String getAccountTypeInfo() {
        return "Cuenta Estudiantil - Sin saldo mínimo, Sin comisión mensual, Para titulares menores de 24 años";
    }

    @Override
    public String toString() {
        return String.format("Cuenta Estudiantil [ID: %d, Saldo: %s, Titular: %s%s, Fecha de creación: %s, Estado: %s]",
                getId(),
                getBalance(),
                getPrimaryOwner().getName(),
                getSecondaryOwner() != null ? ", Cotitular: " + getSecondaryOwner().getName() : "",
                getCreationDate(),
                getStatus());
    }

}
