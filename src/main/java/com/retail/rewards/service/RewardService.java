package com.retail.rewards.service;

import com.retail.rewards.dto.RewardDTO;

import java.time.LocalDate;

public interface RewardService {
    RewardDTO getCustomerRewards(String customerId, LocalDate start, LocalDate end);
}
