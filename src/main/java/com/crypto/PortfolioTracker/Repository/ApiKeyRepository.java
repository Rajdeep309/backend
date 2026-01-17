package com.crypto.PortfolioTracker.Repository;

import com.crypto.PortfolioTracker.Model.ApiKey;
import com.crypto.PortfolioTracker.Model.Exchange;
import com.crypto.PortfolioTracker.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKeyProjection> findByUser_IdAndExchange_Name(Long userId, String exchangeName);

    interface ApiKeyProjection {
        String getApiKey();
        String getApiSecret();
        User getUser();
        Exchange getExchange();
    }

    boolean existsByApiKey(String apiKey);
}
