package io.github.felix.bank_back.controller.account;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.service.account.account.impl.AccountServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Validated
@Tag(name = "Cuentas", description = "Operaciones relacionadas con cuentas bancarias")
public class AccountController {

    private final AccountServiceImpl accountService;

    public AccountController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Mis cuentas", description = "Devuelve las cuentas del titular autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas encontradas")
    })
    @PreAuthorize("hasRole('ACCOUNT_HOLDER')")
    @GetMapping("/me")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccounts(Principal principal) {
        List<AccountResponseDTO> accounts = accountService.getMyAccounts(principal.getName());
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Obtener cuentas por propietarios", description = "Devuelve las cuentas filtradas por propietario principal y secundario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas encontradas"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/owner")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByOwners(
            @Parameter(description = "ID del propietario principal") @RequestParam Long primaryOwnerId,
            @Parameter(description = "ID del propietario secundario (opcional)") @RequestParam(required = false) Long secondaryOwnerId) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByPrimaryOwnerAndSecondaryOwner(primaryOwnerId, secondaryOwnerId);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Obtener cuentas por propietario principal", description = "Devuelve las cuentas asociadas al propietario principal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas encontradas"),
            @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/primary-owner/{id}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByPrimaryOwner(
            @Parameter(description = "ID del propietario principal") @PathVariable Long id) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByPrimaryOwnerId(id);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Obtener cuentas por propietario secundario", description = "Devuelve las cuentas asociadas al propietario secundario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas encontradas"),
            @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/secondary-owner/{id}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsBySecondaryOwner(
            @Parameter(description = "ID del propietario secundario") @PathVariable Long id) {
        List<AccountResponseDTO> accounts = accountService.getAccountsBySecondaryOwnerId(id);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Obtener cuentas por estado", description = "Devuelve las cuentas filtradas por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas encontradas"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByStatus(
            @Parameter(description = "Estado de la cuenta") @PathVariable AccountStatus status) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByStatus(status);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Obtener cuentas por tipo", description = "Devuelve las cuentas filtradas por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas encontradas"),
            @ApiResponse(responseCode = "400", description = "Tipo inválido")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByType(
            @Parameter(description = "Tipo de cuenta") @PathVariable AccountType type) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByType(type);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Aplicar penalización por bajo balance", description = "Aplica penalización a cuentas con balance bajo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Penalización aplicada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/penalty/low-balance")
    public ResponseEntity<List<AccountResponseDTO>> applyPenaltyToAccountsWithLowBalance() {
        List<AccountResponseDTO> accounts = accountService.applyPenaltyToAccountsWithLowBalance();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Aplicar penalización a cuentas de estudiantes con balance negativo", description = "Aplica penalización a cuentas de estudiantes con balance negativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Penalización aplicada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/penalty/student-negative")
    public ResponseEntity<List<AccountResponseDTO>> applyPenaltyToStudentAccountsWithNegativeBalance() {
        List<AccountResponseDTO> accounts = accountService.applyPenaltyToStudentAccountsWithNegativeBalance();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Aplicar tarifa de mantenimiento a cuentas checking", description = "Aplica la tarifa de mantenimiento a cuentas checking que la tengan pendiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarifa aplicada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/maintenance/checking")
    public ResponseEntity<List<AccountResponseDTO>> getCheckingAccountsWithMaintenanceFeeDue() {
        List<AccountResponseDTO> accounts = accountService.getCheckingAccountsWithMaintenanceFeeDue();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Aplicar interés a cuentas de ahorro", description = "Aplica el interés a cuentas de ahorro que lo tengan pendiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interés aplicado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/interest/savings")
    public ResponseEntity<List<AccountResponseDTO>> getSavingsAccountsWithInterestDue() {
        List<AccountResponseDTO> accounts = accountService.getSavingsAccountsWithInterestDue();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Aplicar interés a cuentas de tarjeta de crédito", description = "Aplica el interés a cuentas de tarjeta de crédito que lo tengan pendiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interés aplicado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/interest/credit-card")
    public ResponseEntity<List<AccountResponseDTO>> getCreditCardAccountsWithInterestDue() {
        List<AccountResponseDTO> accounts = accountService.getCreditCardAccountsWithInterestDue();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Eliminar cuenta", description = "Elimina una cuenta por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "ID de la cuenta a eliminar") @PathVariable Long id) {
        boolean deleted = accountService.deleteAccountById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
