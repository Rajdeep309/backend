package com.crypto.PortfolioTracker.Exchange;

import com.crypto.PortfolioTracker.DTO.AddressVerificationDTO;

public interface EtherScanService {

    void validateConnection(String apiKey) throws Exception;

    AddressVerificationDTO isAddressRiskFree(String address, int chainId, String apiKey);
}
