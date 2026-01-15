package com.crypto.PortfolioTracker.Controller;

import com.crypto.PortfolioTracker.DTO.ApiResponse;
import com.crypto.PortfolioTracker.DTO.AddApiKeyRequest;
import com.crypto.PortfolioTracker.Service.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apiKey")
@CrossOrigin("*")
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    @PostMapping("/public/addExchange")
    public ResponseEntity<ApiResponse<String>> exchangeConnector(@RequestBody AddApiKeyRequest exchangeInfo) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        apiKeyService.addApiKey(userId, exchangeInfo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>("Success", null));
    }
}
