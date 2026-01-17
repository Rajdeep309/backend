package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.BinanceTradesDTO;
import com.crypto.PortfolioTracker.DTO.TradeDTO;
import com.crypto.PortfolioTracker.ENUMs.Side;
import com.crypto.PortfolioTracker.ENUMs.WalletTypes;
import com.crypto.PortfolioTracker.Exception.ResourceNotFoundException;
import com.crypto.PortfolioTracker.Exchange.BinanceService;
import com.crypto.PortfolioTracker.Model.Exchange;
import com.crypto.PortfolioTracker.Model.Holding;
import com.crypto.PortfolioTracker.Model.Trade;
import com.crypto.PortfolioTracker.Model.User;
import com.crypto.PortfolioTracker.Repository.ApiKeyRepository;
import com.crypto.PortfolioTracker.Repository.HoldingRepository;
import com.crypto.PortfolioTracker.Repository.TradeRepository;
import com.crypto.PortfolioTracker.Util.EncryptionUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@AllArgsConstructor

@Service
public class TradeServiceImplementation implements TradeService {

    private BinanceService binanceService;

    private TradeRepository tradeRepository;

    private ApiKeyRepository apiKeyRepository;

    private HoldingRepository holdingRepository;

    private EncryptionUtil encryptionUtil;


    @Override
    public List<TradeDTO> syncFullHistory(Long userId) throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {

        ApiKeyRepository.ApiKeyProjection credentials = apiKeyRepository.findByUser_IdAndExchange_Name(userId, "Binance")
                .orElseThrow(() -> new ResourceNotFoundException("API key not found"));

        String apiKey = encryptionUtil.decrypt(credentials.getApiKey());
        String apiSecret = encryptionUtil.decrypt(credentials.getApiSecret());
        Exchange exchange = credentials.getExchange();

        List<TradeDTO> allTrades = new ArrayList<>();
        List<String> symbols = binanceService.fetchSymbols();

        for (String symbol : symbols) {
            String assetSymbol = extractAssetSymbol(symbol);
            if (assetSymbol == null) continue;

            allTrades.addAll(processAssetTrades(userId, assetSymbol, symbol, apiKey, apiSecret, null, exchange));
        }
        return allTrades;
    }

    @Override
    public List<TradeDTO> syncIncremental(Long userId) throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {

        ApiKeyRepository.ApiKeyProjection credentials = apiKeyRepository.findByUser_IdAndExchange_Name(userId, "Binance")
                .orElseThrow(() -> new ResourceNotFoundException("API key not found"));

        String apiKey = encryptionUtil.decrypt(credentials.getApiKey());
        String apiSecret = encryptionUtil.decrypt(credentials.getApiSecret());
        Exchange exchange = credentials.getExchange();

        List<Object[]> maxTimes = tradeRepository.findAllMaxExecutedAtByUserId(userId);
        List<TradeDTO> allTrades = new ArrayList<>();

        for (Object[] row : maxTimes) {
            String assetSymbol = (String) row[0];
            LocalDateTime lastExecutedAt = (LocalDateTime) row[1];

            // Reconstruct symbol - logic assumes USDT pair for incremental
            String symbol = assetSymbol + "USDT";

            allTrades.addAll(processAssetTrades(userId, assetSymbol, symbol, apiKey, apiSecret, lastExecutedAt, exchange));
        }
        return allTrades;
    }

    private String extractAssetSymbol(String symbol) {
        if (symbol.endsWith("USDT")) return symbol.substring(0, symbol.length() - 4);
        if (symbol.endsWith("BTC") || symbol.endsWith("BNB") || symbol.endsWith("ETH")) return symbol.substring(0, symbol.length() - 3);
        return null;
    }

    @Transactional
    private List<TradeDTO> processAssetTrades(Long userId, String assetSymbol, String symbol, String apiKey, String apiSecret, LocalDateTime lastExecutedAt, Exchange exchange) throws InterruptedException, NoSuchAlgorithmException, InvalidKeyException {

        List<BinanceTradesDTO> trades = binanceService.fetchTrades(apiKey, apiSecret, lastExecutedAt, symbol);
        List<TradeDTO> processedTrades = new ArrayList<>();

        Thread.sleep(500);
        if (trades.isEmpty()) return processedTrades;

        // Load current holding state
        var currentHolding = holdingRepository.findByUser_IdAndAssetSymbolAndWalletType(userId, assetSymbol, WalletTypes.EXCHANGE);
        BigDecimal averageCost = currentHolding.map(HoldingRepository.HoldingProjection::getAvgCost).orElse(BigDecimal.ZERO);
        BigDecimal totalQuantity = currentHolding.map(HoldingRepository.HoldingProjection::getQuantity).orElse(BigDecimal.ZERO);

        for (BinanceTradesDTO trade : trades) {
            Side side = trade.isBuyer() ? Side.BUY : Side.SELL;

            if (side == Side.BUY) {
                BigDecimal currentTotalValue = averageCost.multiply(totalQuantity);
                BigDecimal newTradeCost = trade.price().multiply(trade.qty()).add(trade.commission());
                totalQuantity = totalQuantity.add(trade.qty());
                averageCost = currentTotalValue.add(newTradeCost).divide(totalQuantity, 8, RoundingMode.HALF_UP);
            } else {
                totalQuantity = totalQuantity.subtract(trade.qty());
            }

            tradeRepository.save(
                    new Trade(
                            new User(userId),
                            assetSymbol,
                            trade.qty(),
                            side,
                            trade.price(),
                            trade.commission(),
                            exchange,
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(trade.time()), ZoneOffset.UTC)
                    )
            );

            processedTrades.add(new TradeDTO(assetSymbol, trade.qty(), side, trade.price()));
        }

        boolean updated = holdingRepository.updateHoldingDetails(userId, assetSymbol, WalletTypes.EXCHANGE, totalQuantity, averageCost);
        if (!updated) {
            holdingRepository.save(
                    new Holding(
                            new User(userId),
                            assetSymbol,
                            totalQuantity,
                            averageCost,
                            exchange,
                            WalletTypes.EXCHANGE,
                            null
                    )
            );
        }

        return processedTrades;
    }
}
