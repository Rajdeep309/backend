package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.UserCredentialDTO;
import com.crypto.PortfolioTracker.Exception.ResourceNotFoundException;
import com.crypto.PortfolioTracker.Model.User;
import com.crypto.PortfolioTracker.Repository.UserRepository;
import com.crypto.PortfolioTracker.Util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor

@Service
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtUtil jwtUtil;

    @Override
    public UserCredentialDTO logIn(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
        if(passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.GenerateToken(user.getId(), email);
            return new UserCredentialDTO(user.getId(), user.getName(), token);
        }
        throw new ResourceNotFoundException("User not found");
    }

    @Override
    public UserCredentialDTO signUp(String name, String email, String password) {

        User user = new User(LocalDateTime.now(), email, name, passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        String token = jwtUtil.GenerateToken(savedUser.getId(), email);
        return new UserCredentialDTO(savedUser.getId(), name, token);
    }
}
