package com.crypto.PortfolioTracker.Repository;

import com.crypto.PortfolioTracker.Model.ScamToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScamTokenRepository extends JpaRepository<ScamToken, Long> {

    boolean existsByContractAddress(String address);
}
