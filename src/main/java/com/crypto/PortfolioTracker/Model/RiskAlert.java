package com.crypto.PortfolioTracker.Model;

import com.crypto.PortfolioTracker.ENUMs.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "risk_alerts")
public class RiskAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String assetSymbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    @Column(nullable = false)
    private String details;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public RiskAlert(AlertType alertType, String assetSymbol, String details, User user) {
        this.alertType = alertType;
        this.assetSymbol = assetSymbol;
        this.details = details;
        this.user = user;
    }
}
