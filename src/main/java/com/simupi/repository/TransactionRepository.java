package com.simupi.repository;

import com.simupi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface for polymorphic Transaction entity persistence.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByUtr(String utr);

    List<Transaction> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);

    @Query("SELECT t FROM Transaction t WHERE (t.sender.id = :userId OR t.receiver.id = :userId) ORDER BY t.createdAt DESC")
    List<Transaction> findAllUserTransactions(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM SendMoneyTransaction t " +
           "WHERE t.senderAccount.id = :accountId " +
           "AND t.status = com.simupi.model.enums.TransactionStatus.SUCCESS " +
           "AND t.createdAt >= :startOfDay")
    BigDecimal calculateTotalDebitedToday(@Param("accountId") Long accountId, @Param("startOfDay") LocalDateTime startOfDay);
}
