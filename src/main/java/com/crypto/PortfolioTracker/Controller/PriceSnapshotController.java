package com.crypto.PortfolioTracker.Controller;

import com.crypto.PortfolioTracker.DTO.ApiResponse;
import com.crypto.PortfolioTracker.DTO.PriceSnapshotDTO;
import com.crypto.PortfolioTracker.Service.PriceSnapshotService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor

@RestController
@RequestMapping("/api/price-snapshots")
public class PriceSnapshotController {

    private PriceSnapshotService priceSnapshotService;

    @GetMapping("/public/get-price-snapshots")
    public ResponseEntity<ApiResponse<List<PriceSnapshotDTO>>> getPriceSnapshots(
            @RequestParam(name = "assetSymbol") String assetSymbol
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        List<PriceSnapshotDTO> response = priceSnapshotService.fetchPriceSnapshotsByAssetSymbol(assetSymbol);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>("Success", response));
    }
}
