package io.github.felix.bank_back.service.account.account.impl;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.model.account.Checking;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.repository.account.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void getAccountsByStatus_returnsAccounts() {
        Checking acc = new Checking();
        acc.setStatus(AccountStatus.ACTIVE);
        acc.setBalance(new Money(BigDecimal.valueOf(100)));
        acc.setPenaltyFee(new Money(BigDecimal.valueOf(40)));
        AccountHolder owner = new AccountHolder();
        owner.setId(1L);
        owner.setName("Test Owner");
        acc.setPrimaryOwner(owner);
        AccountHolder secondaryOwner = new AccountHolder();
        secondaryOwner.setId(2L);
        secondaryOwner.setName("Secondary Owner");
        acc.setSecondaryOwner(secondaryOwner);
        when(accountRepository.findByStatus(AccountStatus.ACTIVE)).thenReturn(List.of(acc));
        List<AccountResponseDTO> result = accountService.getAccountsByStatus(AccountStatus.ACTIVE);
        assertThat(result).isNotNull();
        verify(accountRepository).findByStatus(AccountStatus.ACTIVE);
    }

    @Test
    void getAccountsByPrimaryOwnerId_returnsAccounts() {
        Checking acc = new Checking();
        acc.setStatus(AccountStatus.ACTIVE);
        acc.setBalance(new Money(BigDecimal.valueOf(200)));
        acc.setPenaltyFee(new Money(BigDecimal.valueOf(40)));
        AccountHolder owner = new AccountHolder();
        owner.setId(10L);
        owner.setName("Owner 10");
        acc.setPrimaryOwner(owner);
        AccountHolder secondaryOwner = new AccountHolder();
        secondaryOwner.setId(20L);
        secondaryOwner.setName("Secondary 20");
        acc.setSecondaryOwner(secondaryOwner);
        when(accountRepository.findByPrimaryOwnerId(10L)).thenReturn(List.of(acc));
        List<AccountResponseDTO> result = accountService.getAccountsByPrimaryOwnerId(10L);
        assertThat(result).isNotNull();
        verify(accountRepository).findByPrimaryOwnerId(10L);
    }

    @Test
    void getAccountsBySecondaryOwnerId_returnsAccounts() {
        Checking acc = new Checking();
        acc.setStatus(AccountStatus.FROZEN);
        acc.setBalance(new Money(BigDecimal.valueOf(300)));
        acc.setPenaltyFee(new Money(BigDecimal.valueOf(50)));
        AccountHolder owner = new AccountHolder();
        owner.setId(11L);
        owner.setName("Owner 11");
        acc.setPrimaryOwner(owner);
        AccountHolder secondaryOwner = new AccountHolder();
        secondaryOwner.setId(21L);
        secondaryOwner.setName("Secondary 21");
        acc.setSecondaryOwner(secondaryOwner);
        when(accountRepository.findBySecondaryOwnerId(21L)).thenReturn(List.of(acc));
        List<AccountResponseDTO> result = accountService.getAccountsBySecondaryOwnerId(21L);
        assertThat(result).isNotNull();
        verify(accountRepository).findBySecondaryOwnerId(21L);
    }

    @Test
    void getAccountsByType_returnsAccounts() {
        Checking acc = new Checking();
        acc.setStatus(AccountStatus.ACTIVE);
        acc.setBalance(new Money(BigDecimal.valueOf(400)));
        acc.setPenaltyFee(new Money(BigDecimal.valueOf(60)));
        AccountHolder owner = new AccountHolder();
        owner.setId(12L);
        owner.setName("Owner 12");
        acc.setPrimaryOwner(owner);
        AccountHolder secondaryOwner = new AccountHolder();
        secondaryOwner.setId(22L);
        secondaryOwner.setName("Secondary 22");
        acc.setSecondaryOwner(secondaryOwner);
        when(accountRepository.findByAccountType(acc.getAccountType())).thenReturn(List.of(acc));
        List<AccountResponseDTO> result = accountService.getAccountsByType(acc.getAccountType());
        assertThat(result).isNotNull();
        verify(accountRepository).findByAccountType(acc.getAccountType());
    }

    @Test
    void deleteAccountById_returnsTrue_whenDeleted() {
        when(accountRepository.existsById(99L)).thenReturn(true);
        doNothing().when(accountRepository).deleteById(99L);
        boolean result = accountService.deleteAccountById(99L);
        assertThat(result).isTrue();
        verify(accountRepository).existsById(99L);
        verify(accountRepository).deleteById(99L);
    }
}