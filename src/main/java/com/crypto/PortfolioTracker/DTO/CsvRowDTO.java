package com.crypto.PortfolioTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CsvRowDTO {

    private String asset;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private BigDecimal quantity;
    private BigDecimal profit;
    private LocalDateTime date;
}
