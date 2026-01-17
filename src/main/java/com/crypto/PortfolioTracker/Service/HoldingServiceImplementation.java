package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.AddressVerificationDTO;
import com.crypto.PortfolioTracker.DTO.BinanceHoldingsDTO;
import com.crypto.PortfolioTracker.DTO.HoldingDTO;
import com.crypto.PortfolioTracker.DTO.HoldingResponse;
import com.crypto.PortfolioTracker.ENUMs.WalletTypes;
import com.crypto.PortfolioTracker.Exception.ResourceNotFoundException;
import com.crypto.PortfolioTracker.Exception.RiskAlertException;
import com.crypto.PortfolioTracker.Exchange.BinanceService;
import com.crypto.PortfolioTracker.Exchange.CryptoScamDbService;
import com.crypto.PortfolioTracker.Exchange.EtherScanService;
import com.crypto.PortfolioTracker.Model.*;
import com.crypto.PortfolioTracker.Repository.*;
import com.crypto.PortfolioTracker.Util.EncryptionUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor

@Service
public class HoldingServiceImplementation implements HoldingService {

    private BinanceService binanceService;

    private ApiKeyRepository apiKeyRepository;

    private HoldingRepository holdingRepository;

    private EncryptionUtil encryptionUtil;

    private EtherScanService etherScanService;

    private CryptoScamDbService cryptoScamDbService;

    private ExchangeRepository exchangeRepository;

    private RiskAlertRepository riskAlertRepository;

    private ScamTokenRepository scamTokenRepository;

    @Override
    public List<HoldingResponse> exchangeWalletRefresh(Long userId) throws NoSuchAlgorithmException, InvalidKeyException {

        Optional<ApiKeyRepository.ApiKeyProjection> credentials = apiKeyRepository.findByUser_IdAndExchange_Name(userId, "Binance");
        if(credentials.isPresent()) {

            User user = credentials.get().getUser();
            Exchange exchange = credentials.get().getExchange();

            String apiKey = encryptionUtil.decrypt(credentials.get().getApiKey());
            String apiSecret = encryptionUtil.decrypt(credentials.get().getApiSecret());

            List<BinanceHoldingsDTO> binanceHoldings = binanceService.fetchHoldings(apiKey, apiSecret);

            Map<String, HoldingResponse> validHoldingsMap = new HashMap<>();
            for (BinanceHoldingsDTO dto : binanceHoldings) {
                BigDecimal total = dto.getFree().add(dto.getLocked());
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    validHoldingsMap.put(dto.getAsset(), new HoldingResponse(dto.getAsset(), total, BigDecimal.ZERO));
                }
            }

            List<Holding> existingHoldings = holdingRepository.findByUser_IdAndExchange_IdAndWalletType(user.getId(), exchange.getId(), WalletTypes.EXCHANGE);
            List<Holding> holdingsToUpdateInDb = new ArrayList<>();

            for(Holding dbHolding : existingHoldings) {
                String symbol = dbHolding.getAssetSymbol();

                if(validHoldingsMap.containsKey(symbol)) {

                    BigDecimal newQty = validHoldingsMap.get(symbol).getQuantity();
                    if(dbHolding.getQuantity().compareTo(newQty) != 0) {
                        dbHolding.setQuantity(newQty);
                        holdingsToUpdateInDb.add(dbHolding);
                    }
                    validHoldingsMap.remove(symbol);
                }
            }

            if(!holdingsToUpdateInDb.isEmpty()) {
                holdingRepository.saveAll(holdingsToUpdateInDb);
            }

            List<HoldingResponse> newHoldingsToInsert = new ArrayList<>(validHoldingsMap.values());
            if(!newHoldingsToInsert.isEmpty()) {
                UpdateHoldingsDB(newHoldingsToInsert, user.getId(), exchange, WalletTypes.EXCHANGE);
            }

            List<HoldingResponse> finalResponse = existingHoldings.stream()
                    .map(h -> new HoldingResponse(
                            h.getAssetSymbol(),
                            h.getQuantity(),
                            h.getAvgCost()
                    ))
                    .collect(Collectors.toList());

            finalResponse.addAll(newHoldingsToInsert);

            return finalResponse;
        }
        else {
            throw new ResourceNotFoundException("Data not found");
        }
    }

    @Override
    public List<HoldingResponse> manualWalletRefresh(Long userId) {
        return holdingRepository.findByUserAndWalletType(new User(userId), WalletTypes.WALLET)
                .orElse(new ArrayList<>());
    }

    private Holding findExistingEntry(Long userId, String assetSymbol, String address) {

        if(address != null) {
            return holdingRepository.findByUserIdAndWalletTypeAndAssetSymbolAndAddress(userId, WalletTypes.WALLET, assetSymbol, address);
        }
        else {
            return holdingRepository.findByUserIdAndAssetSymbolAndWalletTypeAndAddressIsNull(userId, assetSymbol, WalletTypes.WALLET);
        }
    }

    @Override
    public HoldingResponse manualAddOrEdit(Long userId, HoldingDTO holdingInfo) {

        HoldingResponse holdingResponse = new HoldingResponse
                (
                        holdingInfo.getAssetSymbol(),
                        holdingInfo.getQuantity(),
                        holdingInfo.getAvgCost()
                );

        Holding holding = findExistingEntry(userId, holdingInfo.getAssetSymbol(), holdingInfo.getAddress());
        if(holding != null) {
            holding.setQuantity(holdingInfo.getQuantity());
            holding.setAvgCost(holdingInfo.getAvgCost());
            holdingRepository.save(holding);
        }
        else {

            Exchange exchange = null;
            String address = holdingInfo.getAddress();
            if(address != null) {

                if(scamTokenRepository.existsByContractAddress(address)) {
                    throw new RiskAlertException("Address contains risk");
                }

                ApiKeyRepository.ApiKeyProjection credentials = apiKeyRepository.findByUser_IdAndExchange_Name(userId, "EtherScan")
                        .orElseThrow(() -> new ResourceNotFoundException("Exchange not found"));

                AddressVerificationDTO dto = etherScanService.isAddressRiskFree(address, holdingInfo.getChain().getId(), encryptionUtil.decrypt(credentials.getApiKey()));
                if(dto.isRisk()) {

                    riskAlertRepository.save(
                            new RiskAlert(
                                    dto.getAlertType(),
                                    holdingInfo.getAssetSymbol(),
                                    dto.getDetails(),
                                    credentials.getUser()
                            )
                    );

                    scamTokenRepository.save(
                            new ScamToken(
                                    holdingInfo.getChain().toString(),
                                    address,
                                    dto.getRiskLevel(),
                                    "EtherScan"
                            )
                    );
                    throw new RiskAlertException("Address contains risk");
                }

                dto = cryptoScamDbService.isAddressRiskFree(address);
                if(dto.isRisk()) {

                    riskAlertRepository.save(
                            new RiskAlert(
                                    dto.getAlertType(),
                                    holdingInfo.getAssetSymbol(),
                                    dto.getDetails(),
                                    new User(userId)
                            )
                    );

                    scamTokenRepository.save(
                            new ScamToken(
                                    holdingInfo.getChain().toString(),
                                    address,
                                    dto.getRiskLevel(),
                                    "CryptoScamDB"
                            )
                    );
                    throw new RiskAlertException("Address contains risk");
                }
            }
            else {
                exchange = exchangeRepository.findByName("Binance")
                        .orElse(null);
            }

            UpdateHoldingsDB(
                    List.of(holdingResponse),
                    userId,
                    exchange,
                    WalletTypes.WALLET
            );
        }

        return holdingResponse;
    }

    private void UpdateHoldingsDB(List<HoldingResponse> activeBalances, Long userId, Exchange exchange, WalletTypes walletType) {

        List<Holding> holdingsToSave = activeBalances.stream()
                .map(dto -> {
                    return new Holding(new User(userId)
                            , dto.getAssetSymbol()
                            , dto.getQuantity()
                            , dto.getAvgCost()
                            , exchange
                            , walletType
                            , null);
                })
                .toList();

        holdingRepository.saveAll(holdingsToSave);
    }
}
