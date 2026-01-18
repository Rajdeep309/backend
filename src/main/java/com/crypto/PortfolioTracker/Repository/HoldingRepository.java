package com.crypto.PortfolioTracker.Repository;

import com.crypto.PortfolioTracker.DTO.HoldingResponse;
import com.crypto.PortfolioTracker.ENUMs.WalletTypes;
import com.crypto.PortfolioTracker.Model.Holding;
import com.crypto.PortfolioTracker.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    Holding findByUserIdAndAssetSymbolAndWalletTypeAndAddressIsNull(Long userId, String assetSymbol, WalletTypes walletTypes);

    interface HoldingProjection {
        BigDecimal getQuantity();
        BigDecimal getAvgCost();
    }
    Optional<HoldingProjection> findByUser_IdAndAssetSymbolAndWalletType(Long userId, String assetSymbol, WalletTypes walletType);

    @Query("SELECT new com.crypto.PortfolioTracker.DTO.HoldingResponse(h.assetSymbol, h.quantity, h.avgCost) " +
            "FROM Holding h " +
            "WHERE h.user = :user AND h.walletType = :walletType")
    Optional<List<HoldingResponse>> findByUserAndWalletType(User user, WalletTypes walletType);

    Holding findByUserIdAndWalletTypeAndAssetSymbolAndAddress(Long userId, WalletTypes walletType, String assetSymbol, String address);

    List<Holding> findByUser_IdAndExchange_IdAndWalletType(Long userId, Long exchangeId, WalletTypes walletType);

    @Modifying
    @Transactional
    @Query("UPDATE Holding h SET h.quantity = :qty, h.avgCost = :avgCost, h.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE h.user.id = :userId AND h.assetSymbol = :symbol AND h.walletType =:walletType")
    boolean updateHoldingDetails(@Param("userId") Long userId,
                             @Param("symbol") String symbol,
                             @Param("walletType") WalletTypes walletType,
                             @Param("qty") BigDecimal qty,
                             @Param("avgCost") BigDecimal avgCost);

    List<Holding> findByUser_Id(Long userId);

    List<Holding> findByUser_IdAndAssetSymbol(Long userId, String assetSymbol);
}
