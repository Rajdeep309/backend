package com.crypto.PortfolioTracker.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "price_snapshots")
public class PriceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String assetSymbol;

    @Column(nullable = false)
    private BigDecimal priceUsd;

    @Column(nullable = false)
    private BigDecimal marketCap;

    @Column(nullable = false)
    private String source;

    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime capturedAt;

    public PriceSnapshot(String assetSymbol, BigDecimal marketCap, BigDecimal priceUsd, String source) {
        this.assetSymbol = assetSymbol;
        this.marketCap = marketCap;
        this.priceUsd = priceUsd;
        this.source = source;
    }
}
