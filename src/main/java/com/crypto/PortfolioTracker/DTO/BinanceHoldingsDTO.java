package com.crypto.PortfolioTracker.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceHoldingsDTO {

    private String asset;

    private BigDecimal free;

    private BigDecimal locked;
}
