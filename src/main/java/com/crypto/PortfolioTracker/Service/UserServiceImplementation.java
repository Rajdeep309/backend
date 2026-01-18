package com.crypto.PortfolioTracker.Service;

import com.crypto.PortfolioTracker.DTO.UserCredentialDTO;
import com.crypto.PortfolioTracker.DTO.ForgetPasswordResponseDTO;
import com.crypto.PortfolioTracker.Exception.ResourceNotFoundException;
import com.crypto.PortfolioTracker.Model.User;
import com.crypto.PortfolioTracker.Repository.UserRepository;
import com.crypto.PortfolioTracker.Util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@AllArgsConstructor

@Service
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtUtil jwtUtil;

    private JavaMailSender mailSender;

    @Override
    public UserCredentialDTO logIn(String email, String password) {

        UserRepository.UserDetailsProjection user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        if(passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.GenerateToken(user.getId(), email);
            return new UserCredentialDTO(user.getId(), user.getName(), token);
        }
        throw new ResourceNotFoundException("Invalid email or password");
    }

    @Override
    public UserCredentialDTO signUp(String name, String email, String password) {

        User user = new User(LocalDateTime.now(), email, name, passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        String token = jwtUtil.GenerateToken(savedUser.getId(), email);
        return new UserCredentialDTO(savedUser.getId(), name, token);
    }

    @Override
    public ForgetPasswordResponseDTO forgetPassword(String email) {

        UserRepository.UserDetailsProjection user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        Long id = user.getId();
        String name = user.getName();
        String otp = generateOTP();
        sendOtpEmail(email, otp);

        return new ForgetPasswordResponseDTO(id, name, jwtUtil.GenerateToken(id, email), otp);
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        userRepository.resetPassword(email, passwordEncoder.encode(newPassword));
    }

    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    private void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("${spring.mail.username}");
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your one-time password for resetting your account is: " + otp +
                "\nThis code will expire in 5 minutes.");

        mailSender.send(message);
    }
}
