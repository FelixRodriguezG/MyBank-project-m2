package io.github.felix.bank_back.service.account.account.impl;

import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.dto.account.TransferDTO;
import io.github.felix.bank_back.model.account.Account;
import io.github.felix.bank_back.model.account.Checking;
import io.github.felix.bank_back.model.account.Savings;
import io.github.felix.bank_back.model.account.CreditCard;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.repository.account.AccountRepository;
import io.github.felix.bank_back.service.account.account.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    // Filtra cuentas por ID del titular principal o del segundo titular
    @Override
    public List<AccountResponseDTO> getAccountsByPrimaryOwnerAndSecondaryOwner(Long primaryOwnerId,
            Long secondaryOwnerId) {
        return accountRepository.findByPrimaryOwnerIdOrSecondaryOwnerId(primaryOwnerId, secondaryOwnerId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Filtra cuentas por ID del titular principal
    @Override
    public List<AccountResponseDTO> getAccountsByPrimaryOwnerId(Long primaryOwnerId) {
        return accountRepository.findByPrimaryOwnerId(primaryOwnerId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Filtra cuentas por ID del segundo titular
    @Override
    public List<AccountResponseDTO> getAccountsBySecondaryOwnerId(Long secondaryOwnerId) {
        return accountRepository.findBySecondaryOwnerId(secondaryOwnerId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Filtra cuentas por estado (ACTIVE, FROZEN)
    @Override
    public List<AccountResponseDTO> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Filtra cuentas por tipo (CHECKING, SAVINGS, CREDIT_CARD, STUDENT)
    @Override
    public List<AccountResponseDTO> getAccountsByType(AccountType accountType) {
        return accountRepository.findByAccountType(accountType)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Método para aplicar penalización a cuentas con saldo inferior al mínimo
    @Override
    public List<AccountResponseDTO> applyPenaltyToAccountsWithLowBalance() {
        return accountRepository.findByBalanceAmountLessThanMinimumBalance()
                .stream()
                .peek(acc -> {
                    // Aplicar la penalización
                    Money penaltyFee = acc.getPenaltyFee();

                    acc.setBalance(new Money(acc.getBalance().decreaseAmount(penaltyFee)));
                    accountRepository.save(acc);
                })
                .map(this::toDTO)
                .toList();
    }

    // Metodo para aplicar penalización a cuentas Student con saldo negativo
    @Override
    public List<AccountResponseDTO> applyPenaltyToStudentAccountsWithNegativeBalance() {
        return accountRepository.findStudentAccountsByBalanceLessThanZero()
                .stream()
                .peek(acc -> {
                    // Aplicar la penalización
                    Money penaltyFee = acc.getPenaltyFee();

                    acc.setBalance(new Money(acc.getBalance().decreaseAmount(penaltyFee)));
                    accountRepository.save(acc);
                })
                .map(this::toDTO)
                .toList();
    }

    // Metodo para aplicar mantenimiento a las cuentas Checking
    @Override
    public List<AccountResponseDTO> getCheckingAccountsWithMaintenanceFeeDue() {
        return accountRepository.findCheckingAccountsByLastMaintenanceFeeDateBeforeToday()
                .stream()
                .peek(acc -> {
                    // Aplicar la tarifa de mantenimiento
                    if (acc instanceof Checking checkingAcc) {
                        Money maintenanceFee = checkingAcc.getMonthlyMaintenanceFee();
                        // Si la cuenta es tipo Checking, actualizar la fecha del último mantenimiento
                        checkingAcc.setBalance(new Money(acc.getBalance().decreaseAmount(maintenanceFee)));
                        checkingAcc.setLastMaintenanceFeeDate(LocalDate.now());
                        accountRepository.save(checkingAcc);
                    }

                })
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<AccountResponseDTO> getSavingsAccountsWithInterestDue() {
        return accountRepository.findSavingsAccountsByLastInterestAppliedDateBeforeToday()
                .stream()
                .peek(acc -> {
                    // Aplicar el interés mensual
                    if (acc instanceof Savings savingsAcc) {
                        savingsAcc.applyAnnualInterest();
                        accountRepository.save(savingsAcc);

                    }
                }).map(this::toDTO)
                .toList();
    }

    @Override
    public List<AccountResponseDTO> getCreditCardAccountsWithInterestDue() {
        return accountRepository.findCreditCardAccountsByLastInterestAppliedDateBeforeToday()
                .stream()
                .peek(acc -> {
                    // Aplicar el interés mensual para tarjetas de crédito
                    if (acc instanceof CreditCard creditCard) {
                        if (creditCard.applyMonthlyInterest()) {
                            accountRepository.save(creditCard);
                        }
                    }
                })
                .map(this::toDTO)
                .toList();
    }

    // Eliminar cuenta por ID
    @Override
    public boolean deleteAccountById(Long accountId) {
        if (accountRepository.existsById(accountId)) {
            accountRepository.deleteById(accountId);
            return true;
        }
        return false;
    }

    // --- Nuevos métodos para titulares ---
    @Override
    public List<AccountResponseDTO> getMyAccounts(String username) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getPrimaryOwner().getName().equals(username)
                        || (a.getSecondaryOwner() != null && a.getSecondaryOwner().getName().equals(username)))
                .map(this::toDTO)
                .toList();
    }

    @Override
    public AccountResponseDTO getMyAccountForUser(Long accountId, String username) {
        Account acc = accountRepository.findById(accountId).orElse(null);
        if (acc == null)
            return null;
        boolean owns = acc.getPrimaryOwner().getName().equals(username)
                || (acc.getSecondaryOwner() != null && acc.getSecondaryOwner().getName().equals(username));
        if (!owns)
            return null;
        return toDTO(acc);
    }

    @Override
    public AccountResponseDTO transfer(TransferDTO dto, String username) {
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new IllegalArgumentException("Monto inválido");
        }
        Account from = accountRepository.findById(dto.getSenderId()).orElse(null);
        Account to = accountRepository.findById(dto.getReceiverId()).orElse(null);
        if (from == null || to == null) {
            throw new java.util.NoSuchElementException("Cuenta no encontrada");
        }
        boolean owns = from.getPrimaryOwner().getName().equals(username)
                || (from.getSecondaryOwner() != null && from.getSecondaryOwner().getName().equals(username));
        if (!owns) {
            throw new SecurityException("Usuario no autorizado para transferir desde esta cuenta");
        }
        if (dto.getSecretKey() == null || !dto.getSecretKey().equals(to.getSecretKey())) {
            throw new SecurityException("Secret key inválida para la cuenta destino");
        }
        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());
        if (from.getBalance().getAmount().compareTo(amount) < 0) {
            throw new IllegalStateException("Fondos insuficientes");
        }
        from.setBalance(new Money(from.getBalance().decreaseAmount(amount)));
        to.setBalance(new Money(to.getBalance().increaseAmount(amount)));
        accountRepository.save(from);
        accountRepository.save(to);
        return toDTO(from);
    }

    // ================================================
    // Auxiliar: Convierte Account a AccountResponseDTO
    // ================================================
    private AccountResponseDTO toDTO(Account account) {
        // Mapeo manual de Account a AccountResponseDTO
        return new AccountResponseDTO(
                account.getId(),
                account.getBalance().getAmount(),
                account.getCreationDate(),
                account.getStatus(),
                account.getAccountType(),
                account.getPenaltyFee().getAmount(),
                AccountHolderDTO.fromEntity(account.getPrimaryOwner()),
                AccountHolderDTO.fromEntity(account.getSecondaryOwner()));
    }

}
