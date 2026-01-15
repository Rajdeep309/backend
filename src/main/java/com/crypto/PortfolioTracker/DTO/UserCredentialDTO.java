package com.crypto.PortfolioTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserCredentialDTO {

    private Long id;

    private String name;

    private String token;
}
