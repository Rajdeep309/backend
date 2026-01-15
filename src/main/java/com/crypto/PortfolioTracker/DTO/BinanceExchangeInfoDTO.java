package com.crypto.PortfolioTracker.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceExchangeInfoDTO {

    private List<BinanceSymbolsDTO> symbols;
}
