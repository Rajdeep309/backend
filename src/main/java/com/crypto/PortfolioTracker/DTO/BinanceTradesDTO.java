package com.crypto.PortfolioTracker.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BinanceTradesDTO(
        BigDecimal price,
        BigDecimal qty,
        BigDecimal commission,
        long time,
        @JsonProperty("isBuyer")
        boolean isBuyer
) {}
