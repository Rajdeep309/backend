package com.crypto.PortfolioTracker.Exchange;

import com.crypto.PortfolioTracker.DTO.AddressVerificationDTO;
import com.crypto.PortfolioTracker.ENUMs.AlertType;
import com.crypto.PortfolioTracker.ENUMs.RiskLevel;
import com.crypto.PortfolioTracker.Exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CryptoScamDbServiceImplementation implements CryptoScamDbService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://api.cryptoscamdb.org";

    @Override
    public AddressVerificationDTO isAddressRiskFree(String address) {

        String url = BASE_URL + "/v1/address/" + address;

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response == null) throw new ExternalServiceException("Something went wrong");

        AddressVerificationDTO dto = new AddressVerificationDTO(false, AlertType.NONE, RiskLevel.LOW, null);

        boolean success = response.path("success").asBoolean();
        if(success) {
            dto.setRisk(true);
            dto.setRiskLevel(RiskLevel.HIGH);
        }

        JsonNode result = response.path("result");
        if(result != null && result.isArray() && !result.isEmpty()) {

            String category = result.get(0).get("category").asText();
            String description = result.get(0).get("description").asText();

            if("Phishing".equalsIgnoreCase(category)) {
                dto.setAlertType(AlertType.RUGPULL_WARNING);
                dto.setDetails(description);
            }
        }

        return dto;
    }
}
