package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.AddApiKeyRequest;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ApiKeyService {
    void addApiKey(Long userId, AddApiKeyRequest request) throws Exception;
}
