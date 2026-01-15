package com.crypto.PortfolioTracker.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "apiKeys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "exchange_id", nullable = false)
    private Exchange exchange;

    @Column(nullable = false, unique = true)
    private String apiKey;

    private String apiSecret;

    @Column(nullable = false)
    private String label;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ApiKey(String apiKey, String apiSecret, LocalDateTime createdAt, Exchange exchange, String label, User user) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.createdAt = createdAt;
        this.exchange = exchange;
        this.label = label;
        this.user = user;
    }
}
