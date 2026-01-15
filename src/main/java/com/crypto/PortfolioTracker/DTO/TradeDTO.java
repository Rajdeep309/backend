package com.crypto.PortfolioTracker.DTO;

import com.crypto.PortfolioTracker.ENUMs.Side;

import java.math.BigDecimal;

public record TradeDTO(String assetSymbol,
                       BigDecimal quantity,
                       Side side,
                       BigDecimal price) {
}
