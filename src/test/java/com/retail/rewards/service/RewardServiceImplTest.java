package com.retail.rewards.service;

import com.retail.rewards.dto.RewardDTO;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RewardServiceImplTest {

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Mock
    private TransactionRepository transactionRepository;

    private final String customerId = "C001";
    private Customer testCustomer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testCustomer = new Customer();
        testCustomer.setCustomerId(customerId);  // updated to match your actual field
        testCustomer.setCustomerName("Vishal Saste");
    }

    @Test
    public void testGetCustomerRewards() {
        LocalDate start = LocalDate.of(2025, 3, 1);
        LocalDate end = LocalDate.of(2025, 5, 31);

        Transaction t1 = new Transaction(1L, 120.0, LocalDate.of(2025, 3, 15), testCustomer);
        Transaction t2 = new Transaction(2L, 90.0, LocalDate.of(2025, 5, 5), testCustomer);

        List<Transaction> mockTransactions = Arrays.asList(t1, t2);

        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, start, end))
                .thenReturn(mockTransactions);

        RewardDTO result = rewardService.getCustomerRewards(customerId, start, end);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(2, result.getTransactions().size());
        assertEquals(90, result.getTransactions().get(0).getRewardPoints());
        assertEquals(40, result.getTransactions().get(1).getRewardPoints());
        assertEquals(130, result.getTotalRewards());

        Map<String, Integer> monthly = result.getMonthlyRewards();
        assertEquals(90, monthly.get("2025-03"));
        assertEquals(40, monthly.get("2025-05"));
    }

    @Test
    public void testCalculateRewards() {
        assertEquals(90, rewardService.calculateRewards(120.0));
        assertEquals(40, rewardService.calculateRewards(90.0));
        assertEquals(0, rewardService.calculateRewards(50.0));
        assertEquals(10, rewardService.calculateRewards(60.0));
        assertEquals(70, rewardService.calculateRewards(110.0));
    }
}
