package com.crypto.PortfolioTracker.Repository;

import com.crypto.PortfolioTracker.Model.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {
}
