package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.UserCredentialDTO;
import com.crypto.PortfolioTracker.DTO.ForgetPasswordResponseDTO;

public interface UserService {

    UserCredentialDTO logIn(String email, String password);

    UserCredentialDTO signUp(String name, String email, String password);

    ForgetPasswordResponseDTO forgetPassword(String email);

    void resetPassword(String email, String newPassword);
}
