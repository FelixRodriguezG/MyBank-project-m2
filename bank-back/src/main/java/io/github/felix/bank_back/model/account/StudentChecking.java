package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
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

    // Las cuentas estudiantiles NO tienen saldo mínimo ni comisión mensual
    // Solo heredan los campos básicos de Account

    // * Constructor con AccountHolder(propietario principal)
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
        validateStudentEligibility(primaryOwner);
    }

    // * Constructor con AccountHolder(propietario principal) y AccountHolder(propietario secundario)
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        validateStudentEligibility(primaryOwner);
        if (secondaryOwner != null) {
            validateStudentEligibility(secondaryOwner);
        }
    }

    //* Constructor con currency personalizado y propietario principal
    //* El balance ya viene con la currency, pero validamos que sea consistente
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner, String currencyCode) {
        super(balance, secretKey, primaryOwner);
        validateStudentEligibility(primaryOwner);
        // El balance ya viene con la currency, pero validamos que sea consistente
        Currency currency = Currency.getInstance(currencyCode);
        if (!balance.getCurrencyCode().equals(currency)) {
            throw new IllegalArgumentException("Currency code mismatch between balance and specified currency");
        }
    }

    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner,
                           AccountHolder secondaryOwner, String currencyCode) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        validateStudentEligibility(primaryOwner);
        if (secondaryOwner != null) {
            validateStudentEligibility(secondaryOwner);
        }

        Currency currency = Currency.getInstance(currencyCode);
        if (!balance.getCurrencyCode().equals(currency)) {
            throw new IllegalArgumentException("Currency code mismatch between balance and specified currency");
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

    // * Método para obtener el tipo de cuenta
    public String getAccountType() {
        return "Student Checking";
    }

    public String getAccountTypeInfo() {
        return "Student Checking Account - No minimum balance, No monthly maintenance fee, For account holders under 24";
    }

    @Override
    public String toString() {
        return String.format("Student Checking [ID: %d, Balance: %s, Owner: %s%s, CreationDate: %s, Status: %s]",
                getId(),
                getBalance(),
                getPrimaryOwner().getName(),
                getSecondaryOwner() != null ? ", Secondary Owner: " + getSecondaryOwner().getName() : "",
                getCreationDate(),
                getStatus());
    }

}
