package com.crypto.PortfolioTracker.Controller;

import com.crypto.PortfolioTracker.DTO.ApiResponse;
import com.crypto.PortfolioTracker.DTO.TradeDTO;
import com.crypto.PortfolioTracker.Service.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@AllArgsConstructor

@RestController
@CrossOrigin("*")
@RequestMapping("/api/trade")
public class TradeController {

    private TradeService tradeService;

    @PostMapping("/public/fetch-all-trades")
    public ResponseEntity<ApiResponse<List<TradeDTO>>> fetchFullTrades() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {

        Long userId = getLoggedInUserId();

        List<TradeDTO> trades = tradeService.syncFullHistory(userId);
        return new ResponseEntity<>(new ApiResponse<>("Success", trades)
                , HttpStatus.OK);
    }

    @PostMapping("/public/fetch-incremental-trades")
    public ResponseEntity<ApiResponse<List<TradeDTO>>> fetchIncrementalTrades() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {

        Long userId = getLoggedInUserId();

        List<TradeDTO> trades = tradeService.syncIncremental(userId);
        return new ResponseEntity<>(new ApiResponse<>("Success", trades)
                , HttpStatus.OK);
    }

    private Long getLoggedInUserId() {
        return Long.parseLong(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );
    }
}
