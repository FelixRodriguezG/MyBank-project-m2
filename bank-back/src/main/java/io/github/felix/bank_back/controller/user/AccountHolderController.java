package io.github.felix.bank_back.controller.user;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.dto.account.TransferDTO;
import io.github.felix.bank_back.service.account.account.impl.AccountServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/holder")
@Validated
@Tag(name = "Titulares", description = "Operaciones del titular de cuenta")
public class AccountHolderController {

    private final AccountServiceImpl accountService;

    public AccountHolderController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Mis cuentas", description = "Lista todas las cuentas del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido")
    })
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccounts(@AuthenticationPrincipal UserDetails user) {
        var accounts = accountService.getMyAccounts(user.getUsername());
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Detalle de una cuenta propia", description = "Obtiene una cuenta si pertenece al titular autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "403", description = "No pertenece al titular")
    })
    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountResponseDTO> getMyAccount(
            @Parameter(description = "ID de la cuenta") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        var dto = accountService.getMyAccountForUser(id, user.getUsername());
        if (dto == null) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Transferir entre cuentas", description = "Realiza una transferencia si el titular es dueño de la cuenta origen")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferencia realizada"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "422", description = "Fondos insuficientes")
    })
    @PostMapping("/transfers")
    public ResponseEntity<AccountResponseDTO> transfer(@RequestBody TransferDTO dto,
            @AuthenticationPrincipal UserDetails user) {
        try {
            var updated = accountService.transfer(dto, user.getUsername());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(422).build();
        }
    }
}
