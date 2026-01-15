package com.crypto.PortfolioTracker.DTO;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AddApiKeyRequest {

    private String apiKey;

    private String apiSecret;

    private String exchangeName;

    private String label;

}
