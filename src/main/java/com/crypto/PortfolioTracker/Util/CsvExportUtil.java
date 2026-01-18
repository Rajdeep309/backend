package com.crypto.PortfolioTracker.Util;

import com.crypto.PortfolioTracker.DTO.CsvRowDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CsvExportUtil {

    public String generateCsv(List<CsvRowDTO> rows) {

        StringBuilder sb = new StringBuilder();
        sb.append("Asset,BuyPrice,SellPrice,Quantity,Profit,Date\n");

        for (CsvRowDTO row : rows) {
            sb.append(row.getAsset()).append(",")
                    .append(row.getBuyPrice()).append(",")
                    .append(row.getSellPrice()).append(",")
                    .append(row.getQuantity()).append(",")
                    .append(row.getProfit()).append(",")
                    .append(row.getDate()).append("\n");
        }

        return sb.toString();
    }
}
