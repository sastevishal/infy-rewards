package com.retail.rewards.controller;

import com.retail.rewards.dto.RewardDTO;
import com.retail.rewards.exceptionhandler.NoTransactionFoundException;
import com.retail.rewards.service.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private static final Logger logger = LoggerFactory.getLogger(RewardController.class);

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Endpoint to fetch customer rewards within a given date range.
     *
     * @param customerId Customer ID
     * @param startDate  Start date of transaction period (yyyy-MM-dd)
     * @param endDate    End date of transaction period (yyyy-MM-dd)
     * @return ResponseEntity containing RewardDTO with status 200 OK
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<RewardDTO> getCustomerRewards(
            @PathVariable String customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Received request to fetch rewards for customerId={} from {} to {}", customerId, startDate, endDate);

        if (startDate.isAfter(endDate)) {
            logger.warn("Invalid date range: startDate {} is after endDate {}", startDate, endDate);
            throw new IllegalArgumentException("Start date must be before end date.");
        }

        RewardDTO rewardData = rewardService.getCustomerRewards(customerId, startDate, endDate);

        if (rewardData == null || rewardData.getTransactions() == null || rewardData.getTransactions().isEmpty()) {
            logger.warn("No transactions found for customerId={} within the given period", customerId);
            throw new NoTransactionFoundException("No transactions found for the given period.");
        }

        logger.info("Successfully fetched rewards for customerId={}", customerId);
        return ResponseEntity.ok(rewardData);
    }
}
