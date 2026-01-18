package com.crypto.PortfolioTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ForgetPasswordResponseDTO {

    private Long id;

    private String name;

    private String token;

    private String otp;

}
