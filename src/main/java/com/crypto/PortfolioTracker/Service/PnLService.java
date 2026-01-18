package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.AssetPnLResponse;
import com.crypto.PortfolioTracker.DTO.CsvRowDTO;
import com.crypto.PortfolioTracker.DTO.PortfolioPnLResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PnLService {

    PortfolioPnLResponse calculatePortfolioPnL(Long userId);

    AssetPnLResponse calculateAssetPnL(Long userId, String assetSymbol);

    BigDecimal calculateRealizedPnL(Long userId);

    List<CsvRowDTO> generateCsvReport(Long userId);

    String generateTaxHint(BigDecimal realizedPnL);
}
