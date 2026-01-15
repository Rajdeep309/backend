package com.crypto.PortfolioTracker.Model;

import com.crypto.PortfolioTracker.ENUMs.RiskLevel;
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
@Table(name = "scam_tokens")
public class ScamToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contractAddress;

    @Column(nullable = false)
    private String chain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private String source;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastSeen;

    public ScamToken(String chain, String contractAddress, RiskLevel riskLevel, String source) {
        this.chain = chain;
        this.contractAddress = contractAddress;
        this.riskLevel = riskLevel;
        this.source = source;
    }
}
