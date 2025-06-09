package com.retail.rewards.controller;

import com.retail.rewards.dto.RewardDTO;
import com.retail.rewards.exceptionhandler.NoTransactionFoundException;
import com.retail.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardDTO> getCustomerRewards(
            @PathVariable String customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }

        RewardDTO rewardData = rewardService.getCustomerRewards(customerId, startDate, endDate);

        if (rewardData == null || rewardData.getTransactions().isEmpty()) {
            throw new NoTransactionFoundException("No transactions found for the given period.");
        }

        return new ResponseEntity<>(rewardData, HttpStatus.OK);
    }

}
