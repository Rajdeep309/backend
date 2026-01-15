package com.crypto.PortfolioTracker.Repository;

import com.crypto.PortfolioTracker.Model.PriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {

    void deleteByCapturedAtBefore(LocalDateTime cutoffDate);

    interface PriceSnapshotProjection {

        String getAssetSymbol();

        BigDecimal getPriceUsd();

        LocalDateTime getCapturedAt();
    }
    Optional<List<PriceSnapshotProjection>> findByAssetSymbolOrderByCapturedAtAsc(String assetSymbol);
}
