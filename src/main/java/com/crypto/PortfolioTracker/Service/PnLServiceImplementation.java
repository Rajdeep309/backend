package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.AssetPnLResponse;
import com.crypto.PortfolioTracker.DTO.CsvRowDTO;
import com.crypto.PortfolioTracker.DTO.PortfolioPnLResponse;
import com.crypto.PortfolioTracker.Model.Holding;
import com.crypto.PortfolioTracker.Model.Trade;
import com.crypto.PortfolioTracker.Repository.HoldingRepository;
import com.crypto.PortfolioTracker.Repository.PriceSnapshotRepository;
import com.crypto.PortfolioTracker.Repository.TradeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@AllArgsConstructor

@Service
public class PnLServiceImplementation implements PnLService {

    private HoldingRepository holdingRepository;

    private PriceSnapshotRepository priceSnapshotRepository;

    private TradeRepository tradeRepository;

    @Override
    public PortfolioPnLResponse calculatePortfolioPnL(Long userId) {

        List<Holding> holdings = holdingRepository.findByUser_Id(userId);

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        for (Holding holding : holdings) {

            BigDecimal quantity = holding.getQuantity();
            BigDecimal avgCost = holding.getAvgCost();
            totalInvested = totalInvested.add(quantity.multiply(avgCost));

            var prices = priceSnapshotRepository
                    .findByAssetSymbolOrderByCapturedAtAsc(
                            holding.getAssetSymbol())
                    .orElse(List.of());

            if (!prices.isEmpty()) {
                BigDecimal latestPrice =
                        prices.get(prices.size() - 1).getPriceUsd();
                currentValue =
                        currentValue.add(quantity.multiply(latestPrice));
            }
        }

        return new PortfolioPnLResponse(
                totalInvested,
                currentValue,
                currentValue.subtract(totalInvested)
        );
    }

    @Override
    public AssetPnLResponse calculateAssetPnL(Long userId, String assetSymbol) {

        List<Holding> holdings =
                holdingRepository.findByUser_IdAndAssetSymbol(userId, assetSymbol);

        if (holdings.isEmpty()) {
            throw new RuntimeException("No holdings found for asset: " + assetSymbol);
        }
        var prices = priceSnapshotRepository
                .findByAssetSymbolOrderByCapturedAtAsc(assetSymbol)
                .orElseThrow(() -> new RuntimeException("Price not found for asset: " + assetSymbol));

        BigDecimal latestPrice =
                prices.get(prices.size() - 1).getPriceUsd();

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;

        for (Holding h : holdings) {
            BigDecimal qty = h.getQuantity();
            BigDecimal cost = h.getAvgCost();

            totalQty = totalQty.add(qty);
            totalInvested = totalInvested.add(qty.multiply(cost));
        }
        BigDecimal avgCost = totalInvested.divide(
                totalQty, 10, RoundingMode.HALF_UP);

        BigDecimal currentValue = totalQty.multiply(latestPrice);
        BigDecimal pnl = currentValue.subtract(totalInvested);

        return new AssetPnLResponse(
                assetSymbol,
                totalQty,
                avgCost,
                latestPrice,
                totalInvested,
                currentValue,
                pnl
        );
    }

    @Override
    public BigDecimal calculateRealizedPnL(Long userId) {

        List<Trade> trades = tradeRepository.findByUser_IdOrderByExecutedAtAsc(userId);

        Map<String, Queue<Trade>> buyMap = new HashMap<>();
        BigDecimal realizedPnL = BigDecimal.ZERO;

        for (Trade trade : trades) {

            buyMap.putIfAbsent(
                    trade.getAssetSymbol(),
                    new LinkedList<>()
            );

            if (trade.getSide().name().equals("BUY")) {
                buyMap.get(trade.getAssetSymbol()).add(trade);
            } else {

                BigDecimal remainingQty = trade.getQuantity();
                Queue<Trade> buys = buyMap.get(trade.getAssetSymbol());

                while (remainingQty.compareTo(BigDecimal.ZERO) > 0
                        && !buys.isEmpty()) {

                    Trade buy = buys.poll();
                    BigDecimal matchedQty =
                            remainingQty.min(buy.getQuantity());

                    BigDecimal profit =
                            trade.getPrice()
                                    .subtract(buy.getPrice())
                                    .multiply(matchedQty);

                    realizedPnL = realizedPnL.add(profit);
                    remainingQty =
                            remainingQty.subtract(matchedQty);
                }
            }
        }
        return realizedPnL;
    }

    @Override
    public List<CsvRowDTO> generateCsvReport(Long userId) {

        List<Trade> trades =
                tradeRepository.findByUser_IdOrderByExecutedAtAsc(userId);

        Map<String, Queue<Trade>> buyMap = new HashMap<>();
        List<CsvRowDTO> rows = new ArrayList<>();

        for (Trade trade : trades) {

            buyMap.putIfAbsent(
                    trade.getAssetSymbol(),
                    new LinkedList<>()
            );

            if (trade.getSide().name().equals("BUY")) {
                buyMap.get(trade.getAssetSymbol()).add(trade);
            } else {

                BigDecimal remainingQty = trade.getQuantity();
                Queue<Trade> buys = buyMap.get(trade.getAssetSymbol());

                while (remainingQty.compareTo(BigDecimal.ZERO) > 0
                        && !buys.isEmpty()) {

                    Trade buy = buys.poll();
                    BigDecimal matchedQty =
                            remainingQty.min(buy.getQuantity());

                    BigDecimal profit =
                            trade.getPrice()
                                    .subtract(buy.getPrice())
                                    .multiply(matchedQty);

                    rows.add(new CsvRowDTO(
                            trade.getAssetSymbol(),
                            buy.getPrice(),
                            trade.getPrice(),
                            matchedQty,
                            profit,
                            trade.getExecutedAt()
                    ));

                    remainingQty =
                            remainingQty.subtract(matchedQty);
                }
            }
        }
        return rows;
    }

    @Override
    public String generateTaxHint(BigDecimal realizedPnL) {

        String taxHint;
        if (realizedPnL.compareTo(BigDecimal.ZERO) > 0) {
            taxHint = "You made a profit. Capital gains tax may apply.";
        }
        else if (realizedPnL.compareTo(BigDecimal.ZERO) < 0) {
            taxHint = "You made a loss. You may offset this against future gains.";
        }
        else {
            taxHint = "No profit or loss. No tax impact.";
        }

        return taxHint;
    }
}
