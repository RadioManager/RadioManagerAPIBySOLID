package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.TransactionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    List<TransactionDto> getAllTransactions();

    TransactionDto getTransactionById(Long id);

    List<TransactionDto> getTransactionsByUserId(Long userId);

    List<TransactionDto> getTransactionsByAdminId(Long adminId);

    List<TransactionDto> getTransactionsByAdminIdAndUserId(Long adminId, Long userId);

    List<TransactionDto> getTransactionsByDate(LocalDateTime transactionDate);

    TransactionDto createTransaction(TransactionDto transactionDto);

    // TODO(ревью): новый метод — бывшая логика старого TransactionController.createSystemTransaction
    TransactionDto createSystemTransaction(TransactionDto transactionDto);

    boolean deleteTransaction(Long id);

}
