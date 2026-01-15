package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.HoldingDTO;
import com.crypto.PortfolioTracker.DTO.HoldingResponse;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface HoldingService {

    List<HoldingResponse> exchangeWalletRefresh(Long userId) throws NoSuchAlgorithmException, InvalidKeyException;

    List<HoldingResponse> manualWalletRefresh(Long userId);

    HoldingResponse manualAddOrEdit(Long userId, HoldingDTO holdingInfo);
}
