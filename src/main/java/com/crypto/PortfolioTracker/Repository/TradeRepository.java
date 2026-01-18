package com.crypto.PortfolioTracker.Repository;

import com.crypto.PortfolioTracker.Model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query("SELECT t.assetSymbol, MAX(t.executedAt) FROM Trade t WHERE t.user.id = :userId GROUP BY t.assetSymbol")
    List<Object[]> findAllMaxExecutedAtByUserId(@Param("userId") Long userId);

    List<Trade> findByUser_IdOrderByExecutedAtAsc(Long userId);

    List<Trade> findByUser_IdAndAssetSymbolOrderByExecutedAtAsc(
            Long userId,
            String assetSymbol
    );
}
