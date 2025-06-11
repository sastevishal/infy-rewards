package com.retail.rewards.controller;

import com.retail.rewards.dto.RewardDTO;
import com.retail.rewards.dto.TransactionResponse;
import com.retail.rewards.exceptionhandler.NoTransactionFoundException;
import com.retail.rewards.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RewardControllerTest {

    @InjectMocks
    private RewardController rewardController;

    @Mock
    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCustomerRewards_Success() {
        String customerId = "C001";
        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 31);

        List<TransactionResponse> transactions = List.of(
                new TransactionResponse(120.0, LocalDate.of(2025, 3, 15), 90),
                new TransactionResponse(90.0, LocalDate.of(2025, 5, 5), 40)
        );

        RewardDTO rewardDTO = new RewardDTO();
        rewardDTO.setCustomerId(customerId);
        rewardDTO.setStartDate(startDate);
        rewardDTO.setEndDate(endDate);
        rewardDTO.setTotalRewards(130);
        rewardDTO.setTransactions(transactions);
        Map<String, Integer> monthly = new HashMap<>();
        monthly.put("2025-03", 90);
        monthly.put("2025-05", 40);
        rewardDTO.setMonthlyRewards(monthly);

        when(rewardService.getCustomerRewards(customerId, startDate, endDate)).thenReturn(rewardDTO);

        ResponseEntity<RewardDTO> response = rewardController.getCustomerRewards(customerId, startDate, endDate);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(130, response.getBody().getTotalRewards());
        assertEquals(2, response.getBody().getTransactions().size());
    }

    @Test
    void testGetCustomerRewards_InvalidDateRange() {
        String customerId = "C001";
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 31);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> rewardController.getCustomerRewards(customerId, startDate, endDate)
        );

        assertEquals("Start date must be before end date.", thrown.getMessage());
    }

    @Test
    void testGetCustomerRewards_NoTransactionsFound() {
        String customerId = "C001";
        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 31);

        RewardDTO rewardDTO = new RewardDTO();
        rewardDTO.setCustomerId(customerId);
        rewardDTO.setStartDate(startDate);
        rewardDTO.setEndDate(endDate);
        rewardDTO.setTransactions(Collections.emptyList());

        when(rewardService.getCustomerRewards(customerId, startDate, endDate)).thenReturn(rewardDTO);

        assertThrows(NoTransactionFoundException.class, () -> {
            rewardController.getCustomerRewards(customerId, startDate, endDate);
        });
    }
}
