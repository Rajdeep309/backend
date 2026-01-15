package com.crypto.PortfolioTracker.Model;

import com.crypto.PortfolioTracker.ENUMs.Side;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "trades")
public class Trade {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Side side;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal price;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal fee;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "exchange_id", nullable = false)
    private Exchange exchange;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    public Trade(User user
            , String assetSymbol
            , BigDecimal quantity
            , Side side
            , BigDecimal price
            , BigDecimal fee
            , Exchange exchange
            , LocalDateTime executedAt) {

        this.user = user;
        this.assetSymbol = assetSymbol;
        this.quantity = quantity;
        this.side = side;
        this.price = price;
        this.fee = fee;
        this.exchange = exchange;
        this.executedAt = executedAt;
    }
}
