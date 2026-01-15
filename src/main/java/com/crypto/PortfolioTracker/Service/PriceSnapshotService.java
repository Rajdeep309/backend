package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.PriceSnapshotDTO;

import java.util.List;

public interface PriceSnapshotService {

    void savePriceSnapshots();

    void removeOldSnapshots();

    List<PriceSnapshotDTO> fetchPriceSnapshotsByAssetSymbol(String assetSymbol);
}
