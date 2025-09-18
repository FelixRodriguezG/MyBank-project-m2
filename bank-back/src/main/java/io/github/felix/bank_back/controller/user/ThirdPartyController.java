package io.github.felix.bank_back.controller.user;

import io.github.felix.bank_back.dto.transaction.ThirdPartyTransactionDTO;
import io.github.felix.bank_back.service.user.third_party.impl.ThirdPartyServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/third-party")
@Validated
@Tag(name = "Terceros", description = "Operaciones de terceros (depósitos y retiros)")
public class ThirdPartyController {

    private final ThirdPartyServiceImpl thirdPartyService;

    public ThirdPartyController(ThirdPartyServiceImpl thirdPartyService) {
        this.thirdPartyService = thirdPartyService;
    }

    @Operation(summary = "Depositar en cuenta", description = "Tercero deposita en una cuenta usando hashed key y secretKey")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito realizado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "403", description = "Clave o secretKey inválida"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PostMapping("/transactions/deposit")
    public ResponseEntity<Void> deposit(@RequestHeader("X-Hashed-Key") @Parameter(description = "Hashed key del tercero") String hashedKey,
            @RequestBody ThirdPartyTransactionDTO dto) {
        try {
            thirdPartyService.deposit(hashedKey, dto);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(422).build();
        }
    }

    @Operation(summary = "Retirar de cuenta", description = "Tercero retira de una cuenta usando hashed key y secretKey")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retiro realizado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "403", description = "Clave o secretKey inválida"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "422", description = "Fondos insuficientes")
    })
    @PostMapping("/transactions/withdraw")
    public ResponseEntity<Void> withdraw(@RequestHeader("X-Hashed-Key") @Parameter(description = "Hashed key del tercero") String hashedKey,
            @RequestBody ThirdPartyTransactionDTO dto) {
        try {
            thirdPartyService.withdraw(hashedKey, dto);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(422).build();
        }
    }
}
