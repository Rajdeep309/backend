package com.crypto.PortfolioTracker.Exchange;

import com.crypto.PortfolioTracker.DTO.CoinGeckoPriceSnapshotsDTO;

import java.util.List;

public interface CoinGeckoService {

    List<CoinGeckoPriceSnapshotsDTO> fetchPriceSnapshots();
}
