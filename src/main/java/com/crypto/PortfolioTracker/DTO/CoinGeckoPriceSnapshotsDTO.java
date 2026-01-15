package com.crypto.PortfolioTracker.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties
public record CoinGeckoPriceSnapshotsDTO
        (
                String symbol,
                BigDecimal current_price,
                BigDecimal market_cap
        ) {
}
