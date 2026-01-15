package com.crypto.PortfolioTracker.DTO;

import com.crypto.PortfolioTracker.ENUMs.AlertType;
import com.crypto.PortfolioTracker.ENUMs.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AddressVerificationDTO {

    private boolean risk;

    private AlertType alertType;

    private RiskLevel riskLevel;

    private String details;
}
