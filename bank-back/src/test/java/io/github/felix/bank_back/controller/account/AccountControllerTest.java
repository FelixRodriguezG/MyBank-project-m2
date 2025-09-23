package io.github.felix.bank_back.controller.account;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.service.account.account.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @Mock
    private AccountServiceImpl accountService;
    @InjectMocks
    private AccountController accountController;
    @Mock
    private Principal principal;

    @Test
    void getMyAccounts_returnsAccounts() {
        when(principal.getName()).thenReturn("user1");
        AccountResponseDTO dto = new AccountResponseDTO();
        when(accountService.getMyAccounts("user1")).thenReturn(List.of(dto));
        ResponseEntity<List<AccountResponseDTO>> response = accountController.getMyAccounts(principal);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(accountService).getMyAccounts("user1");
    }

    @Test
    void getAccountsByStatus_returnsAccounts() {
        AccountResponseDTO dto = new AccountResponseDTO();
        when(accountService.getAccountsByStatus(AccountStatus.ACTIVE)).thenReturn(List.of(dto));
        ResponseEntity<List<AccountResponseDTO>> response = accountController.getAccountsByStatus(AccountStatus.ACTIVE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(accountService).getAccountsByStatus(AccountStatus.ACTIVE);
    }

    @Test
    void getAccountsByType_returnsAccounts() {
        AccountResponseDTO dto = new AccountResponseDTO();
        when(accountService.getAccountsByType(AccountType.CHECKING)).thenReturn(List.of(dto));
        ResponseEntity<List<AccountResponseDTO>> response = accountController.getAccountsByType(AccountType.CHECKING);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(accountService).getAccountsByType(AccountType.CHECKING);
    }

    @Test
    void deleteAccount_returnsNoContent_whenDeleted() {
        when(accountService.deleteAccountById(1L)).thenReturn(true);
        ResponseEntity<Void> response = accountController.deleteAccount(1L);
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(accountService).deleteAccountById(1L);
    }

    @Test
    void deleteAccount_returnsNotFound_whenNotDeleted() {
        when(accountService.deleteAccountById(2L)).thenReturn(false);
        ResponseEntity<Void> response = accountController.deleteAccount(2L);
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(accountService).deleteAccountById(2L);
    }
}
