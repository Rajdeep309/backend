package com.crypto.PortfolioTracker.Controller;

import com.crypto.PortfolioTracker.DTO.ApiResponse;
import com.crypto.PortfolioTracker.DTO.PriceSnapshotDTO;
import com.crypto.PortfolioTracker.Service.PriceSnapshotService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor

@RestController
@CrossOrigin("*")
@RequestMapping("/api/price-snapshots")
public class PriceSnapshotController {

    private PriceSnapshotService priceSnapshotService;

    @GetMapping("/public/get-price-snapshots")
    public ResponseEntity<ApiResponse<List<PriceSnapshotDTO>>> getPriceSnapshots(
            @RequestParam(name = "assetSymbol") String assetSymbol
    ) {

        Long userId = getLoggedInUserId();

        List<PriceSnapshotDTO> response = priceSnapshotService.fetchPriceSnapshotsByAssetSymbol(assetSymbol);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>("Success", response));
    }

    private Long getLoggedInUserId() {
        return Long.parseLong(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );
    }
}
