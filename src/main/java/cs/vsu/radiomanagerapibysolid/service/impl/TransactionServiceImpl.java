package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.dto.TransactionDto;
import cs.vsu.radiomanagerapibysolid.mapper.TransactionMapper;
import cs.vsu.radiomanagerapibysolid.model.Transaction;
import cs.vsu.radiomanagerapibysolid.repository.TransactionRep;
import cs.vsu.radiomanagerapibysolid.service.inter.TransactionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRep transactionRep;

    private final TransactionMapper mapper;

    @Override
    public List<TransactionDto> getAllTransactions() {
        LOGGER.debug("Fetching all transactions");
        return mapper.toDtoList(transactionRep.findAll());
    }

    @Override
    public TransactionDto getTransactionById(Long id) {
        LOGGER.debug("Fetching transaction by id: {}", id);
        return transactionRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public List<TransactionDto> getTransactionsByUserId(Long userId) {
        LOGGER.debug("Fetching transactions by user id: {}", userId);
        return mapper.toDtoList(transactionRep.findByUserId(userId));
    }

    @Override
    public List<TransactionDto> getTransactionsByAdminId(Long adminId) {
        LOGGER.debug("Fetching transactions by admin id: {}", adminId);
        return mapper.toDtoList(transactionRep.findByAdminId(adminId));
    }

    @Override
    public List<TransactionDto> getTransactionsByAdminIdAndUserId(Long adminId, Long userId) {
        LOGGER.debug("Fetching transactions by admin id: {} and user id: {}", adminId, userId);
        return mapper.toDtoList(transactionRep.findByAdminIdAndUserId(adminId, userId));
    }

    @Override
    public List<TransactionDto> getTransactionsByDate(LocalDateTime transactionDate) {
        LOGGER.debug("Fetching transactions by date {}", transactionDate);
        return mapper.toDtoList(transactionRep.findByTransactionDate(transactionDate));
    }

    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        LOGGER.debug("Creating transaction: {}", transactionDto);
        Transaction newTransaction = mapper.toEntity(transactionDto);
        newTransaction.setId(null);
        newTransaction = transactionRep.save(newTransaction);
        LOGGER.debug("Transaction created: {}", newTransaction);
        return mapper.toDto(newTransaction);
    }

    @Override
    public boolean deleteTransaction(Long id) {
        LOGGER.debug("Deleting transaction by id: {}", id);
        Optional<Transaction> transactionOptional = transactionRep.findById(id);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            transactionRep.delete(transaction);
            LOGGER.debug("Transaction deleted: {}", transaction);
            return true;
        }
        LOGGER.warn("Transaction with id {} not found", id);
        return false;
    }

}
