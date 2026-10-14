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

    private final UserService userService;

    private final TransactionService transactionService;

    // TODO(диплом): деньги хранятся в Double — копейки могут «поплыть» при округлении; для денег правильнее
    //   BigDecimal. И проверка баланса не защищена от двух одновременных списаний у одного пользователя
    //   (оба увидят старый баланс) — лечится оптимистической блокировкой (@Version в User).
    @Override
    public void charge(Long userId, double amount, String description) {
        UserDto user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found: " + userId);
        }
        if (user.getBalance() < amount) {
            throw new ConflictException("Insufficient balance: " + user.getBalance() + ", required: " + amount);
        }

        userService.updateBalance(userId, user.getBalance() - amount);

        TransactionDto transaction = new TransactionDto();
        transaction.setUserId(userId);
        transaction.setAmount(-amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setDescription(description);
        transactionService.createSystemTransaction(transaction);

        LOGGER.info("Charged {} from user {}: {}", amount, userId, description);
    }

}
