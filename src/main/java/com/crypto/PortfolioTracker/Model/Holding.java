package com.crypto.PortfolioTracker.Model;

import com.crypto.PortfolioTracker.ENUMs.WalletTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "holdings")
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String assetSymbol;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal avgCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletTypes walletType;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "exchange_id")
    private Exchange exchange;

    private String address;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Holding(User user, String assetSymbol, BigDecimal quantity, BigDecimal avgCost, Exchange exchange, WalletTypes walletType, String address) {
        this.user = user;
        this.assetSymbol = assetSymbol;
        this.exchange = exchange;
        this.quantity = quantity;
        this.avgCost = avgCost;
        this.walletType = walletType;
        this.address = address;
    }
}
