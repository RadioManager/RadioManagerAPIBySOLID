package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.TransactionDto;

public interface BillingService {

    void charge(Long userId, double amount, String description);

    TransactionDto topUp(Long userId, double amount);

}
