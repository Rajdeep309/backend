package com.crypto.PortfolioTracker.DTO;

import lombok.*;

@Data
@AllArgsConstructor

public class ApiResponse<T> {

    private String message;

    private T data;
}
