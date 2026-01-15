package com.crypto.PortfolioTracker.Repository;

import com.crypto.PortfolioTracker.Model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    Optional<Exchange> findByName(String exchangeName);
}
