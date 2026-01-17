package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.AddApiKeyRequest;
import com.crypto.PortfolioTracker.Exception.DuplicateEntryException;
import com.crypto.PortfolioTracker.Exception.ResourceNotFoundException;
import com.crypto.PortfolioTracker.Exchange.BinanceService;
import com.crypto.PortfolioTracker.Exchange.EtherScanService;
import com.crypto.PortfolioTracker.Model.ApiKey;
import com.crypto.PortfolioTracker.Model.Exchange;
import com.crypto.PortfolioTracker.Model.User;
import com.crypto.PortfolioTracker.Repository.ApiKeyRepository;
import com.crypto.PortfolioTracker.Repository.ExchangeRepository;
import com.crypto.PortfolioTracker.Util.EncryptionUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor

@Service
public class ApiKeyServiceImplementation implements ApiKeyService {

    private ExchangeRepository exchangeRepository;

    private ApiKeyRepository apiKeyRepository;

    private BinanceService binanceService;

    private EncryptionUtil encryptionUtil;

    private EtherScanService etherScanService;

    @Override
    public void addApiKey(Long userId, AddApiKeyRequest request) throws Exception {

        if(apiKeyRepository.existsByApiKey(encryptionUtil.encrypt(request.getApiKey()))) {
            throw new DuplicateEntryException("Duplicate entry alert");
        }

        Exchange exchange = exchangeRepository.findByName(request.getExchangeName())
                .orElseThrow(() -> new ResourceNotFoundException("Exchange not found"));

        String apiKey = request.getApiKey();
        String apiSecret = request.getApiSecret();

        if(exchange.getName().equals("Binance")) {
            binanceService.validateConnection(apiKey, apiSecret);
        }
        else {
            etherScanService.validateConnection(apiKey);
        }

        String encryptedApiKey = encryptionUtil.encrypt(apiKey);
        String encryptedSecretKey = apiSecret != null ? encryptionUtil.encrypt(apiSecret) : null;

        apiKeyRepository.save(
                new ApiKey(
                        encryptedApiKey,
                        encryptedSecretKey,
                        LocalDateTime.now(),
                        exchange,
                        request.getLabel(),
                        new User(userId)
                )
        );
    }
}
