package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.CoinGeckoPriceSnapshotsDTO;
import com.crypto.PortfolioTracker.DTO.PriceSnapshotDTO;
import com.crypto.PortfolioTracker.Exchange.CoinGeckoService;
import com.crypto.PortfolioTracker.Model.PriceSnapshot;
import com.crypto.PortfolioTracker.Repository.PriceSnapshotRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor

@Service
public class PriceSnapshotServiceImplementation implements PriceSnapshotService {

    private PriceSnapshotRepository priceSnapshotRepository;

    private CoinGeckoService coinGeckoService;

    @Override
    @Scheduled(fixedRate = 24, timeUnit = TimeUnit.HOURS)
    public void savePriceSnapshots() {

        List<CoinGeckoPriceSnapshotsDTO> geckoPriceSnapshots = coinGeckoService.fetchPriceSnapshots();

        List<PriceSnapshot> priceSnapshots = geckoPriceSnapshots.stream()
                .map(dto -> new PriceSnapshot(
                        dto.symbol().toUpperCase(),
                        dto.market_cap(),
                        dto.current_price(),
                        "CoinGecko")
                )
                .toList();
        priceSnapshotRepository.saveAll(priceSnapshots);
    }

    @Override
    @Scheduled(cron = "0 10 18 * * *")
    @Transactional
    public void removeOldSnapshots() {

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        priceSnapshotRepository.deleteByCapturedAtBefore(cutoffDate);
    }

    @Override
    public List<PriceSnapshotDTO> fetchPriceSnapshotsByAssetSymbol(String assetSymbol) {

        List<PriceSnapshotRepository.PriceSnapshotProjection> priceSnapshots = priceSnapshotRepository.findByAssetSymbolOrderByCapturedAtAsc(assetSymbol)
                .orElse(new ArrayList<>());
        return priceSnapshots.stream()
                .map(priceSnapshot -> new PriceSnapshotDTO(
                        priceSnapshot.getAssetSymbol(),
                        priceSnapshot.getPriceUsd(),
                        priceSnapshot.getCapturedAt()
                        )
                ).toList();
    }
}
