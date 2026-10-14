package cs.vsu.radiomanagerapibysolid.controller;

import cs.vsu.radiomanagerapibysolid.dto.TransactionDto;
import cs.vsu.radiomanagerapibysolid.service.inter.AccessService;
import cs.vsu.radiomanagerapibysolid.service.inter.BillingService;
import cs.vsu.radiomanagerapibysolid.service.inter.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Transactions", description = "Balance top-up and transaction history")
@AllArgsConstructor
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    private final BillingService billingService;

    private final AccessService accessService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all transactions",
            description = "Retrieves a list of all transactions."
    )
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        LOGGER.info("Fetching all transactions");
        List<TransactionDto> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a transaction by its ID."
    )
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        LOGGER.info("Fetching transaction with ID: {}", id);
        TransactionDto transaction = transactionService.getTransactionById(id);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        }

        LOGGER.warn("Transaction with ID {} not found", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("@accessService.isOwnerOrAdmin(#userId)")
    @Operation(
            summary = "Get transactions by user ID",
            description = "Retrieves transactions belonging to a specific user."
    )
    public ResponseEntity<List<TransactionDto>> getTransactionsByUserId(@PathVariable Long userId) {
        LOGGER.info("Fetching transactions for user ID: {}", userId);
        List<TransactionDto> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/admin/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get transactions by admin ID",
            description = "Retrieves transactions associated with a specific administrator."
    )
    public ResponseEntity<List<TransactionDto>> getTransactionsByAdminId(@PathVariable Long adminId) {
        LOGGER.info("Fetching transactions for admin ID: {}", adminId);
        List<TransactionDto> transactions = transactionService.getTransactionsByAdminId(adminId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/admin-user")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get transactions by admin and user IDs",
            description = "Retrieves transactions filtered by both administrator ID and user ID. " +
                    "Provide adminId and userId as query parameters."
    )
    public ResponseEntity<List<TransactionDto>> getTransactionsByAdminUser(@RequestParam Long adminId,
                                                                           @RequestParam Long userId) {
        LOGGER.info("Fetching transactions for admin ID: {} and user ID: {}", adminId, userId);
        List<TransactionDto> transactions = transactionService.getTransactionsByAdminIdAndUserId(adminId, userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get transactions by date",
            description = "Retrieves transactions for the specified transaction date."
    )
    public ResponseEntity<List<TransactionDto>> getTransactionsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        LOGGER.info("Fetching transactions for date: {}", date);
        List<TransactionDto> transactions = transactionService.getTransactionsByDate(date);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/top-up")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Top up balance",
            description = "Adds the given amount to the current user's balance and records a system transaction."
    )
    public ResponseEntity<TransactionDto> topUp(@RequestParam double amount) {
        Long userId = accessService.getCurrentUserId();
        LOGGER.info("User {} is topping up balance by {}", userId, amount);
        TransactionDto transaction = billingService.topUp(userId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create system transaction",
            description = "Creates a system transaction by automatically " +
                    "assigning the system entity's admin ID to the transaction."
    )
    public ResponseEntity<TransactionDto> createSystemTransaction(@RequestBody @Valid TransactionDto transactionDto) {
        LOGGER.info("Creating system transaction: {}", transactionDto);
        TransactionDto created = transactionService.createSystemTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create transaction",
            description = "Creates a new transaction."
    )
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody @Valid TransactionDto transactionDto) {
        LOGGER.info("Creating transaction: {}", transactionDto);
        TransactionDto created = transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete transaction",
            description = "Deletes a transaction by its ID."
    )
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        LOGGER.info("Deleting transaction with ID: {}", id);
        boolean deleted = transactionService.deleteTransaction(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }

        LOGGER.warn("Transaction with ID {} not found for delete", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
