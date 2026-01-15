package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.UserCredentialDTO;

public interface UserService {

    UserCredentialDTO logIn(String email, String password);

    UserCredentialDTO signUp(String name, String email, String password);
}
