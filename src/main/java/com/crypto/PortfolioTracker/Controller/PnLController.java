package com.crypto.PortfolioTracker.Controller;

import com.crypto.PortfolioTracker.DTO.ApiResponse;
import com.crypto.PortfolioTracker.DTO.AssetPnLResponse;
import com.crypto.PortfolioTracker.DTO.PortfolioPnLResponse;
import com.crypto.PortfolioTracker.DTO.RealizedPnLResponse;
import com.crypto.PortfolioTracker.Service.PnLService;
import com.crypto.PortfolioTracker.Util.CsvExportUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@AllArgsConstructor

@RestController
@RequestMapping("/api/pnl")
@CrossOrigin("*")
public class PnLController {

    private PnLService pnLService;

    private CsvExportUtil csvExportUtil;

    @PostMapping("/public/summary")
    public ResponseEntity<ApiResponse<PortfolioPnLResponse>> calculatePortfolioPnL() {

        Long userId = getLoggedInUserId();

        PortfolioPnLResponse response =
                pnLService.calculatePortfolioPnL(userId);

        return ResponseEntity.ok(
                new ApiResponse<>("Success", response)
        );
    }

    @PostMapping("/public/asset/{symbol}")
    public ResponseEntity<ApiResponse<AssetPnLResponse>> calculateAssetPnL(
            @PathVariable String symbol) {

        Long userId = getLoggedInUserId();

        AssetPnLResponse response =
                pnLService.calculateAssetPnL(userId, symbol);

        return ResponseEntity.ok(
                new ApiResponse<>("Success", response)
        );
    }

    @PostMapping("/public/realized")
    public ResponseEntity<ApiResponse<RealizedPnLResponse>> calculateRealizedPnL() {

        Long userId = getLoggedInUserId();

        BigDecimal realizedPnL = pnLService.calculateRealizedPnL(userId);
        String taxHint = pnLService.generateTaxHint(realizedPnL);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Success"
                        , new RealizedPnLResponse(realizedPnL, taxHint)
                )
        );
    }

    @PostMapping("/public/export/csv")
    public ResponseEntity<byte[]> exportCsv() {

        Long userId = getLoggedInUserId();

        String csv = csvExportUtil.generateCsv(
                pnLService.generateCsvReport(userId)
        );

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=pnl-report.csv")
                .header("Content-Type", "text/csv")
                .body(csv.getBytes());
    }

    private Long getLoggedInUserId() {
        return Long.parseLong(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName() // Although one can use "getPrincipal()" to get the user id in long format directly
        );
    }
}
