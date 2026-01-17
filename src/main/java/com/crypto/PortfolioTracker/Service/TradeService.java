package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.TradeDTO;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface TradeService {

    List<TradeDTO> syncFullHistory(Long userId) throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException;

    List<TradeDTO> syncIncremental(Long userId) throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException;
}
