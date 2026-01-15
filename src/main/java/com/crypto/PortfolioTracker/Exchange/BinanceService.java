package com.crypto.PortfolioTracker.Exchange;

import com.crypto.PortfolioTracker.DTO.BinanceHoldingsDTO;
import com.crypto.PortfolioTracker.DTO.BinanceTradesDTO;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

public interface BinanceService {

    void validateConnection(String apiKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException;

    List<BinanceHoldingsDTO> fetchHoldings(String apiKey, String apiSecret) throws NoSuchAlgorithmException, InvalidKeyException;

    List<String> fetchSymbols();

    List<BinanceTradesDTO> fetchTrades(String apiKey, String apiSecret, LocalDateTime lastExecutedAt, String symbol) throws NoSuchAlgorithmException, InvalidKeyException;
}
