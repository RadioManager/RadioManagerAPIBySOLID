package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.dto.TransactionDto;
import cs.vsu.radiomanagerapibysolid.dto.UserDto;
import cs.vsu.radiomanagerapibysolid.exception.ConflictException;
import cs.vsu.radiomanagerapibysolid.exception.NotFoundException;
import cs.vsu.radiomanagerapibysolid.service.inter.BillingService;
import cs.vsu.radiomanagerapibysolid.service.inter.TransactionService;
import cs.vsu.radiomanagerapibysolid.service.inter.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@AllArgsConstructor
public class BillingServiceImpl implements BillingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingServiceImpl.class);

    private static final String TOP_UP_DESCRIPTION = "Пополнение счета";

    private final UserService userService;

    private final TransactionService transactionService;

    @Override
    public void charge(Long userId, double amount, String description) {
        UserDto user = getUserOrThrow(userId);
        if (user.getBalance() < amount) {
            throw new ConflictException("Insufficient balance: " + user.getBalance() + ", required: " + amount);
        }

        userService.updateBalance(userId, user.getBalance() - amount);
        recordSystemTransaction(userId, -amount, description);
        LOGGER.info("Charged {} from user {}: {}", amount, userId, description);
    }

    @Override
    public TransactionDto topUp(Long userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Top-up amount must be positive");
        }
        UserDto user = getUserOrThrow(userId);

        userService.updateBalance(userId, user.getBalance() + amount);
        TransactionDto transaction = recordSystemTransaction(userId, amount, TOP_UP_DESCRIPTION);
        LOGGER.info("Topped up {} to user {}", amount, userId);
        return transaction;
    }

    private UserDto getUserOrThrow(Long userId) {
        UserDto user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found: " + userId);
        }
        return user;
    }

    private TransactionDto recordSystemTransaction(Long userId, double amount, String description) {
        TransactionDto transaction = new TransactionDto();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setDescription(description);
        return transactionService.createSystemTransaction(transaction);
    }

}
