package com.crypto.PortfolioTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class HoldingResponse {

    private String assetSymbol;

    private BigDecimal quantity;

    private BigDecimal avgCost;
}
