package com.crypto.PortfolioTracker.Controller;

import com.crypto.PortfolioTracker.DTO.ApiResponse;
import com.crypto.PortfolioTracker.DTO.HoldingDTO;
import com.crypto.PortfolioTracker.DTO.HoldingResponse;
import com.crypto.PortfolioTracker.Service.HoldingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("api/holding")
@CrossOrigin("*")
public class HoldingController {

    @Autowired
    private HoldingService holdingService;

    @PostMapping("/public/refresh-exchange-holdings") // binance : holding le ke aayega tabe me storee karega and return to frontend
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> refreshHoldingsOfExchangeWallet() throws NoSuchAlgorithmException, InvalidKeyException {

        Long userId = getLoggedInUserId();

        List<HoldingResponse> holdings = holdingService.exchangeWalletRefresh(userId);
        return new ResponseEntity<>(new ApiResponse<>("Success", holdings)
                , HttpStatus.OK);
    }

    @GetMapping("/public/refresh-manual-holdings") // mannually assest ka info dale , user likhega details address me bata raha hai ki kaha se liya hai
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> refreshManualHoldings() {

        Long userId = getLoggedInUserId();

        List<HoldingResponse> holdings = holdingService.manualWalletRefresh(userId);
        return new ResponseEntity<>(new ApiResponse<>("Success", holdings)
                , HttpStatus.OK);
    }

    @PostMapping("/public/manual-add-edit")// mannuaaly edit karega
    public ResponseEntity<ApiResponse<HoldingResponse>> manualAddEdit(@RequestBody HoldingDTO holdingInfo) {

        Long userId = getLoggedInUserId();

        HoldingResponse holding = holdingService.manualAddOrEdit(userId, holdingInfo);
        return new ResponseEntity<>(new ApiResponse<>("Success", holding)
                , HttpStatus.OK);
    }

    @DeleteMapping("/public/delete-manual-holding")
    public ResponseEntity<ApiResponse<String>> manualAddEdit(@RequestParam String assetSymbol) {

        Long userId = getLoggedInUserId();

        holdingService.deleteHolding(userId, assetSymbol);
        return new ResponseEntity<>(new ApiResponse<>("Success", null)
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
