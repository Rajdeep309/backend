package com.crypto.PortfolioTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AssetPnLResponse {

    private String asset;
    private BigDecimal quantity;
    private BigDecimal avgCost;
    private BigDecimal latestPrice;
    private BigDecimal invested;
    private BigDecimal currentValue;
    private BigDecimal unrealizedPnL;
}
