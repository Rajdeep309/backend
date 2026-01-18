package com.crypto.PortfolioTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PortfolioPnLResponse {

    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal unrealizedPnL;
}
