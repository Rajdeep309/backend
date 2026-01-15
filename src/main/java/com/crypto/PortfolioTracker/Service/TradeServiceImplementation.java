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
    public List<TradeDTO> fetchTradesAndUpdateHoldings(Long userId) throws InterruptedException, NoSuchAlgorithmException, InvalidKeyException {

        ApiKeyRepository.ApiKeyProjection credentials = apiKeyRepository.findByUser_IdAndExchange_Name(userId, "Binance")
                .orElseThrow(() -> new ResourceNotFoundException("API key not found"));

        String apiKey = encryptionUtil.decrypt(credentials.getApiKey());
        String apiSecret = encryptionUtil.decrypt(credentials.getApiSecret());
        Exchange exchange = credentials.getExchange();

        List<Object[]> maxTimes = tradeRepository.findAllMaxExecutedAtByUserId(userId);
        Map<String, LocalDateTime> lastExecutionMap = new HashMap<>();
        for (Object[] row : maxTimes) {
            lastExecutionMap.put((String) row[0], (LocalDateTime) row[1]);
        }

        List<TradeDTO> tradeDTOs = new ArrayList<>();

        List<String> symbols = binanceService.fetchSymbols();
        for(String symbol : symbols) {

            String assetSymbol;
            if (symbol.endsWith("USDT")) {
                assetSymbol = symbol.substring(0, symbol.length() - 4);
            } else if (symbol.endsWith("BTC") || symbol.endsWith("BNB") || symbol.endsWith("ETH")) {
                assetSymbol = symbol.substring(0, symbol.length() - 3);
            } else {
                continue;
            }

            LocalDateTime lastExecutedAt = lastExecutionMap.get(assetSymbol);
            List<BinanceTradesDTO> trades = binanceService.fetchTrades(apiKey, apiSecret, lastExecutedAt, symbol);

            Thread.sleep(500);
            if(trades.isEmpty()) continue;

            Optional<HoldingRepository.HoldingProjection> currentHolding = holdingRepository.findByUser_IdAndAssetSymbolAndWalletType(userId, assetSymbol, WalletTypes.EXCHANGE);

            BigDecimal averageCost = BigDecimal.ZERO;
            BigDecimal totalQuantity = BigDecimal.ZERO;

            if(currentHolding.isPresent()) {
                averageCost = currentHolding.get().getAvgCost();
                totalQuantity = currentHolding.get().getQuantity();
            }

            for(BinanceTradesDTO trade : trades) {

                Side side;
                BigDecimal tradeQty = trade.qty();
                BigDecimal tradePrice = trade.price();
                BigDecimal commission = trade.commission();

                if(trade.isBuyer()) {

                    side = Side.BUY;

                    BigDecimal currentTotalValue = averageCost.multiply(totalQuantity);
                    BigDecimal newTradeCost = tradePrice.multiply(tradeQty).add(commission);

                    totalQuantity = totalQuantity.add(tradeQty);

                    averageCost = currentTotalValue.add(newTradeCost)
                            .divide(totalQuantity, 8, RoundingMode.HALF_UP);
                }
                else {
                    side = Side.SELL;
                    totalQuantity = totalQuantity.subtract(tradeQty);
                }

                LocalDateTime executedTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(trade.time()),
                        ZoneOffset.UTC
                );
                tradeRepository.save(
                        new Trade(new User(userId)
                                , assetSymbol
                                , tradeQty
                                , side
                                , tradePrice
                                , commission
                                , exchange
                                , executedTime)
                );

                tradeDTOs.add(
                        new TradeDTO(
                                assetSymbol,
                                tradeQty,
                                side,
                                tradePrice
                        )
                );
            }

            boolean updated = holdingRepository.updateHoldingDetails(userId, assetSymbol, WalletTypes.EXCHANGE, totalQuantity, averageCost);
            if(!updated) {
                Holding holding = new Holding(
                        new User(userId),
                        assetSymbol,
                        totalQuantity,
                        averageCost,
                        exchange,
                        WalletTypes.EXCHANGE,
                        null
                );
                holdingRepository.save(holding);
            }
        }

        return tradeDTOs;
    }
}
