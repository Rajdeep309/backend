package com.crypto.PortfolioTracker.Controller;

import com.crypto.PortfolioTracker.DTO.ApiResponse;
import com.crypto.PortfolioTracker.DTO.UserCredentialDTO;
import com.crypto.PortfolioTracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/auth/public/sign-up")
    public ResponseEntity<ApiResponse<UserCredentialDTO>> signUp(@RequestParam String name, String email, String password) {

        UserCredentialDTO userCredential = userService.signUp(name, email, password);
        return new ResponseEntity<>(new ApiResponse<>("Account created successfully", userCredential)
                , HttpStatus.CREATED);
    }

    @PostMapping("/auth/public/log-in")
    public ResponseEntity<ApiResponse<UserCredentialDTO>> logIn(@RequestParam String email, String password) {

        UserCredentialDTO userCredential = userService.logIn(email, password);
        return new ResponseEntity<>(new ApiResponse<>("Log in successful", userCredential)
                , HttpStatus.OK);
    }
}
