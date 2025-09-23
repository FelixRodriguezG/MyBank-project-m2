package io.github.felix.bank_back.service.account.account.impl;

import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.dto.account.TransferDTO;
import io.github.felix.bank_back.model.account.Account;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.model.transaction.Transaction;
import io.github.felix.bank_back.model.transaction.enums.TransactionType;
import io.github.felix.bank_back.repository.account.AccountRepository;
import io.github.felix.bank_back.repository.transaction.TransactionRepository;
import io.github.felix.bank_back.service.account.account.interfaces.AccountService;
import io.github.felix.bank_back.service.account.checking.interfaces.CheckingService;
import io.github.felix.bank_back.service.account.savings.interfaces.SavingsService;
import io.github.felix.bank_back.service.account.credit_card.interfaces.CreditCardService;
import io.github.felix.bank_back.model.account.StudentChecking;
import io.github.felix.bank_back.repository.account.StudentCheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CheckingService checkingService;

    @Autowired
    private SavingsService savingsService;

    @Autowired
    private CreditCardService creditCardService;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

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

    // Método para aplicar penalización a cuentas con saldo inferior al mínimo (delegando por tipo)
    @Override
    public List<AccountResponseDTO> applyPenaltyToAccountsWithLowBalance() {
        return accountRepository.findByBalanceAmountLessThanMinimumBalance()
                .stream()
                .peek(acc -> {
                    if (acc.getAccountType() == AccountType.CHECKING) {
                        checkingService.checkMinimumBalance(acc.getId());
                    } else if (acc.getAccountType() == AccountType.SAVINGS) {
                        savingsService.checkMinimumBalance(acc.getId());
                    }
                })
                .map(a -> toDTO(accountRepository.findById(a.getId()).orElse(a)))
                .toList();
    }

    // Penalización a cuentas Student con saldo negativo (sin servicio específico definido)
    @Override
    public List<AccountResponseDTO> applyPenaltyToStudentAccountsWithNegativeBalance() {
        List<StudentChecking> cuentas = studentCheckingRepository.findAll();
        for (StudentChecking cuenta : cuentas) {
            if (cuenta.getPrimaryOwner() == null) {
                System.err.println("[ERROR] Cuenta StudentChecking sin propietario: ID=" + cuenta.getId());
                throw new IllegalStateException("Cuenta StudentChecking sin propietario: ID=" + cuenta.getId());
            }
        }
        return accountRepository.findStudentAccountsByBalanceLessThanZero()
                .stream()
                .peek(acc -> {
                    Money penaltyFee = acc.getPenaltyFee();
                    acc.setBalance(new Money(acc.getBalance().decreaseAmount(penaltyFee)));
                    accountRepository.save(acc);
                    // Registrar transacción de penalización
                    transactionRepository.save(new Transaction(new Money(penaltyFee.getAmount(), penaltyFee.getCurrencyCode()),
                            TransactionType.PENALTY_FEE, acc, "Penalty fee for negative balance (student)"));
                })
                .map(this::toDTO)
                .toList();
    }

    // Mantenimiento mensual para Checking (delegado)
    @Override
    public List<AccountResponseDTO> getCheckingAccountsWithMaintenanceFeeDue() {
        return accountRepository.findCheckingAccountsByLastMaintenanceFeeDateBeforeToday()
                .stream()
                .peek(acc -> checkingService.applyMonthlyMaintenanceFee(acc.getId()))
                .map(a -> toDTO(accountRepository.findById(a.getId()).orElse(a)))
                .toList();
    }

    // Interés anual para Savings (delegado)
    @Override
    public List<AccountResponseDTO> getSavingsAccountsWithInterestDue() {
        return accountRepository.findSavingsAccountsByLastInterestAppliedDateBeforeToday()
                .stream()
                .peek(acc -> savingsService.applyInterest(acc.getId()))
                .map(a -> toDTO(accountRepository.findById(a.getId()).orElse(a)))
                .toList();
    }

    // Interés mensual para CreditCard (delegado)
    @Override
    public List<AccountResponseDTO> getCreditCardAccountsWithInterestDue() {
        return accountRepository.findCreditCardAccountsByLastInterestAppliedDateBeforeToday()
                .stream()
                .peek(acc -> creditCardService.applyInterest(acc.getId()))
                .map(a -> toDTO(accountRepository.findById(a.getId()).orElse(a)))
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
    @Transactional
    public AccountResponseDTO transfer(TransferDTO dto, String username) {
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new IllegalArgumentException("Monto inválido");
        }
        Account from = accountRepository.findById(dto.getSenderId()).orElse(null);
        Account to = accountRepository.findById(dto.getReceiverId()).orElse(null);
        if (from == null || to == null) {
            throw new NoSuchElementException("Cuenta no encontrada");
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
        // Actualizar balances
        from.setBalance(new Money(from.getBalance().decreaseAmount(amount)));
        to.setBalance(new Money(to.getBalance().increaseAmount(amount)));
        accountRepository.save(from);
        accountRepository.save(to);
        // Registrar transacción (TRANSFER) con cuenta origen y destino
        Transaction tx = new Transaction(new Money(amount, from.getBalance().getCurrencyCode()), from, to, "Account transfer");
        transactionRepository.save(tx);
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
