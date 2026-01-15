package com.crypto.PortfolioTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PriceSnapshotDTO {

    private String assetSymbol;

    private BigDecimal priceUsd;

    LocalDateTime capturedAt;
}
