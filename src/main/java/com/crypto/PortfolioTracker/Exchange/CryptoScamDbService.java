package com.crypto.PortfolioTracker.Exchange;

import com.crypto.PortfolioTracker.DTO.AddressVerificationDTO;

public interface CryptoScamDbService {
    AddressVerificationDTO isAddressRiskFree(String address);
}
