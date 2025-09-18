package io.github.felix.bank_back.controller.transaction;

import io.github.felix.bank_back.model.transaction.Transaction;
import io.github.felix.bank_back.service.transaction.impl.TransactionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transacciones", description = "Consultas de transacciones")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Listar por rango de fechas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping
    public ResponseEntity<List<Transaction>> getBetweenDates(@RequestParam("start") LocalDateTime start,
                                                             @RequestParam("end") LocalDateTime end) {
        return ResponseEntity.ok(transactionService.getTransactionsBetweenDates(start, end));
    }

    @Operation(summary = "Contar por cuenta y rango de fechas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteo obtenido"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping("/count")
    public ResponseEntity<Long> countByAccountAndRange(@RequestParam("accountId") Long accountId,
                                                        @RequestParam("start") LocalDateTime start,
                                                        @RequestParam("end") LocalDateTime end) {
        long total = transactionService.countTransactionsByAccountAndDateRange(accountId, start, end);
        return ResponseEntity.ok(total);
    }
}
