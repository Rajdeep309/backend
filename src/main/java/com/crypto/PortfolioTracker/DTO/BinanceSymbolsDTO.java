package com.crypto.PortfolioTracker.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BinanceSymbolsDTO(
        String status,
        String symbol
) {
}
