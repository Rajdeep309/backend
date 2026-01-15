package com.crypto.PortfolioTracker.Exchange;

import com.crypto.PortfolioTracker.DTO.CoinGeckoPriceSnapshotsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CoinGeckoImplementation implements CoinGeckoService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<CoinGeckoPriceSnapshotsDTO> fetchPriceSnapshots() {

        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=30&page=1&sparkline=false";

        ResponseEntity<CoinGeckoPriceSnapshotsDTO[]> response = restTemplate.getForEntity(url, CoinGeckoPriceSnapshotsDTO[].class);
        CoinGeckoPriceSnapshotsDTO [] body = response.getBody();

        if(body != null) {
            return Arrays.asList(body);
        }
        else {
            return new ArrayList<>();
        }
    }
}
