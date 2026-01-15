package com.crypto.PortfolioTracker.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceAccountResponseDTO {

    @JsonProperty("balances")
    private List<BinanceHoldingsDTO> holdings;
}