package com.crypto.PortfolioTracker.Exchange;

import com.crypto.PortfolioTracker.DTO.BinanceAccountResponseDTO;
import com.crypto.PortfolioTracker.DTO.BinanceExchangeInfoDTO;
import com.crypto.PortfolioTracker.DTO.BinanceHoldingsDTO;
import com.crypto.PortfolioTracker.DTO.BinanceSymbolsDTO;
import com.crypto.PortfolioTracker.DTO.BinanceTradesDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BinanceServiceImplementation implements BinanceService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BINANCE_API_URL = "https://api.binance.com/api/v3";

    @Override
    public void validateConnection(String apiKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {

        long timeStamp = System.currentTimeMillis() - 2000;
        String queryString = "timestamp=" + timeStamp + "&recvWindow=60000";
        String signature = generateHmacSha256(queryString, secretKey);

        String finalUrl = BINANCE_API_URL + "/account?" + queryString + "&signature=" + signature;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", apiKey);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplate.exchange(finalUrl, HttpMethod.GET, entity, String.class);
    }

    private String generateHmacSha256(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {

        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte [] rawHmac = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : rawHmac) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public List<BinanceHoldingsDTO> fetchHoldings(String apiKey, String apiSecret) throws NoSuchAlgorithmException, InvalidKeyException {

        long timeStamp = System.currentTimeMillis() - 2000;
        String queryString = "timestamp=" + timeStamp + "&recvWindow=60000";
        String signature = generateHmacSha256(queryString, apiSecret);

        String finalUrl = BINANCE_API_URL + "/account?" + queryString + "&signature=" + signature;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", apiKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<BinanceAccountResponseDTO> response = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, BinanceAccountResponseDTO.class);

        BinanceAccountResponseDTO body = response.getBody();
        if(body != null && body.getHoldings() != null) {
            return body.getHoldings();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> fetchSymbols() {

        String finalUrl = BINANCE_API_URL + "/exchangeInfo";

        ResponseEntity<BinanceExchangeInfoDTO> response = restTemplate.getForEntity(finalUrl, BinanceExchangeInfoDTO.class);
        BinanceExchangeInfoDTO body = response.getBody();

        if(body != null && body.getSymbols() != null) {
            return body.getSymbols().stream()
                    .filter(s -> "TRADING".equals(s.status()))
                    .map(BinanceSymbolsDTO::symbol)
                    .collect(Collectors.toList());
        }
        else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<BinanceTradesDTO> fetchTrades(String apiKey, String apiSecret, LocalDateTime lastExecutedAt, String symbol) throws NoSuchAlgorithmException, InvalidKeyException {

        long startTime;
        if (lastExecutedAt != null) {
            startTime = lastExecutedAt.toInstant(ZoneOffset.UTC).toEpochMilli() + 1;
        } else {
            startTime = 0L;
        }

        long timeStamp = System.currentTimeMillis() - 2000;
        String queryParams = "symbol=" + symbol
                + "&startTime=" + startTime
                + "&limit=100"
                + "&recvWindow=60000"
                + "&timestamp=" + timeStamp;
        String signature = generateHmacSha256(queryParams, apiSecret);
        String finalUrl = BINANCE_API_URL + "/myTrades" + "?" + queryParams + "&signature=" + signature;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", apiKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<BinanceTradesDTO[]> response = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                entity,
                BinanceTradesDTO[].class
        );

        if (response.getBody() != null) {
            return Arrays.asList(response.getBody());
        }
        else {
            return new ArrayList<>();
        }
    }
}
