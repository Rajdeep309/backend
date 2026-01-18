package com.crypto.PortfolioTracker.Exchange;

import com.crypto.PortfolioTracker.DTO.AddressVerificationDTO;
import com.crypto.PortfolioTracker.ENUMs.AlertType;
import com.crypto.PortfolioTracker.ENUMs.RiskLevel;
import com.crypto.PortfolioTracker.Exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EtherScanServiceImplementation implements EtherScanService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://api.etherscan.io";

    @Override
    public void validateConnection(String apiKey) {

        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/v2/api")
                .queryParam("chainid", 1)
                .queryParam("module", "stats")
                .queryParam("action", "ethprice")
                .queryParam("apikey", apiKey)
                .toUriString();

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        if (response != null) {
            String status = response.path("status").asText();
            String result = response.path("result").asText();

            if ("0".equals(status)) {
                throw new ExternalServiceException("EtherScan API Error: " + result);
            }
        } else {
            throw new ExternalServiceException("No response received from EtherScan.");
        }
    }

    @Override
    public AddressVerificationDTO isAddressRiskFree(String address, int chainId, String apiKey) {

        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/v2/api")
                .queryParam("chainid", chainId)
                .queryParam("module", "nametag")
                .queryParam("action", "getaddresstag")
                .queryParam("address", address)
                .queryParam("apikey", apiKey)
                .toUriString();

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if(response == null) throw new ExternalServiceException("Something went wrong");

        AddressVerificationDTO dto = new AddressVerificationDTO(false, AlertType.NONE, RiskLevel.LOW, null);

        String status = response.path("status").asText();
        if(status.equals("0")) throw new ExternalServiceException("Invalid address");

        JsonNode result = response.path("result");
        if(result != null && result.isArray() && !result.isEmpty()) {

            String reputation = result.get(0).get("reputation").asText();
            String details = result.get(0).get("notes_1").asText();

            if("2".equals(reputation) || "Suspicious".equalsIgnoreCase(reputation)) {
                dto.setRisk(true);
                dto.setRiskLevel(RiskLevel.HIGH);
                dto.setAlertType(AlertType.CONTRACT_RISK);
                dto.setDetails(details);
            }
        }

        return dto;
    }
}
