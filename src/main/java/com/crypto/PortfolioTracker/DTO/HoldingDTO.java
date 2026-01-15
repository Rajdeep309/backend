package com.crypto.PortfolioTracker.DTO;

import com.crypto.PortfolioTracker.ENUMs.EtherScanChain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class HoldingDTO {

    private String assetSymbol;

    private BigDecimal quantity;

    private BigDecimal avgCost;

    private String address;

    private EtherScanChain chain;
}
