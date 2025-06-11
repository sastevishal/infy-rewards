package com.retail.rewards.repository;

import com.retail.rewards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Transaction entity.
 * <p>
 * Extends JpaRepository to provide CRUD operations and
 * custom query method to retrieve transactions by customer ID and date range.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Retrieves all transactions for a specific customer within a given date range.
     *
     * @param customerId The ID of the customer.
     * @param startDate  The start date of the transaction period.
     * @param endDate    The end date of the transaction period.
     * @return A list of transactions matching the criteria.
     */
    List<Transaction> findByCustomer_CustomerIdAndTransactionDateBetween(String customerId, LocalDate startDate, LocalDate endDate);
}
