package com.retail.rewards.service;

import com.retail.rewards.dto.RewardDTO;
import com.retail.rewards.dto.TransactionResponse;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RewardServiceImpl implements RewardService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public RewardDTO getCustomerRewards(String customerId, LocalDate start, LocalDate end) {
        List<Transaction> transactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, start, end);

        int totalRewards = 0;
        Map<String, Integer> monthlyRewards = new HashMap<>();
        List<TransactionResponse> txResponses = new ArrayList<>();

        for (Transaction tx : transactions) {
            int points = calculateRewards(tx.getAmount());
            totalRewards += points;

            String monthKey = tx.getTransactionDate().getYear() + "-" + String.format("%02d", tx.getTransactionDate().getMonthValue());
            monthlyRewards.put(monthKey, monthlyRewards.getOrDefault(monthKey, 0) + points);

            txResponses.add(new TransactionResponse(tx.getAmount(), tx.getTransactionDate(), points));
        }

        RewardDTO rewardDTO = new RewardDTO();
        rewardDTO.setCustomerId(customerId);
        rewardDTO.setStartDate(start);
        rewardDTO.setEndDate(end);
        rewardDTO.setTotalRewards(totalRewards);
        rewardDTO.setMonthlyRewards(monthlyRewards);
        rewardDTO.setTransactions(txResponses);

        return rewardDTO;
    }

    public int calculateRewards(double amount) {
        int rewards = 0;
        if (amount > 100) {
            rewards += (int) ((amount - 100) * 2) + 50;
        } else if (amount > 50) {
            rewards += (int) (amount - 50);
        }
        return rewards;
    }
}
