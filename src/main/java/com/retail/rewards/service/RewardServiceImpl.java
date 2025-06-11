package com.retail.rewards.service;

import com.retail.rewards.dto.RewardDTO;
import com.retail.rewards.dto.TransactionResponse;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {
    private TransactionRepository transactionRepository;

    public RewardServiceImpl(TransactionRepository transactionRepository) { this.transactionRepository = transactionRepository; }

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Calculates the total, monthly, and transaction-level rewards
     * for a given customer within a specified date range.
     *
     * @param customerId the ID of the customer
     * @param start      the start date for the reward calculation
     * @param end        the end date for the reward calculation
     * @return RewardDTO object containing rewards data
     */
    @Override
    public RewardDTO getCustomerRewards(String customerId, LocalDate start, LocalDate end) {
        List<Transaction> transactions = transactionRepository.findByCustomer_CustomerIdAndTransactionDateBetween(customerId, start, end);

        List<TransactionResponse> txResponses = transactions.stream()
                .map(tx -> new TransactionResponse(
                        tx.getTransactionAmount(),
                        tx.getTransactionDate(),
                        calculateRewards(tx.getTransactionAmount())))
                .collect(Collectors.toList());

        int totalRewards = txResponses.stream()
                .mapToInt(TransactionResponse::getRewardPoints)
                .sum();

        Map<String, Integer> monthlyRewards = txResponses.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getDate().format(MONTH_FORMATTER),
                        Collectors.summingInt(TransactionResponse::getRewardPoints)
                ));

        RewardDTO rewardDTO = new RewardDTO();
        rewardDTO.setCustomerId(customerId);
        rewardDTO.setStartDate(start);
        rewardDTO.setEndDate(end);
        rewardDTO.setTotalRewards(totalRewards);
        rewardDTO.setMonthlyRewards(monthlyRewards);
        rewardDTO.setTransactions(txResponses);

        return rewardDTO;
    }

    /**
     * Calculates reward points:
     * - 2 points for every dollar spent over $100,
     * - 1 point for every dollar spent between $50 and $100.
     *
     * @param amount the transaction amount
     * @return reward points
     */
    public int calculateRewards(double amount) {
        return amount > 100 ? (int) ((amount - 100) * 2 + 50) :
                amount > 50 ? (int) (amount - 50) :
                        0;
    }
}
