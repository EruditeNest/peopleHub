package com.people.hub.authentication;

import com.people.hub.authentication.validation.ValidationToken;
import com.people.hub.authentication.validation.ValidationTokenRepo;
import com.people.hub.authentication.validation.ValidationTokenStatus;
import com.people.hub.core.common.RestApiResponse;
import com.people.hub.core.common.exception.BadRequestException;
import com.people.hub.core.user.User;
import com.people.hub.core.user.UserRepo;
import com.people.hub.security.JwtUtils;
import com.people.hub.security.MyUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${WEBSITE_BASEURL}")
    private String baseUrl;
    private final UserRepo userRepo;
    private final ValidationTokenRepo tokenRepo;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public RestApiResponse loginGenerateToken(String email, String password, boolean rememberMe) {
        try{
            User user = userRepo.findByEmail(email);
            if(user != null) {
                String userPassword = user.getPassword();
                if(passwordEncoder.matches(password, userPassword)) {
                    String token = jwtUtils.createToken(email, user.getRoleId(), user.getUserId(), rememberMe);
                    return RestApiResponse.success(token);
                }
                return RestApiResponse.failure("Incorrect Password");
            }
            return RestApiResponse.failure("User not found");
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public RestApiResponse changePassword(String oldPassword, String newPassword, String confirmPassword, MyUserDetail userDetails) {

        User user = userRepo.findById(userDetails.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("New passwords do not match");
        }
        if (newPassword.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepo.save(user);

        return RestApiResponse.success();
    }

    public RestApiResponse forgotPassword(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            return RestApiResponse.success(); // Don't reveal user existence, Attackers use that for email harvesting.
        }
        UUID uuid = UUID.randomUUID();
        ValidationToken token = new ValidationToken();
        token.setUserId(user.getUserId());
        token.setUuid(uuid);
        token.setStatus(ValidationTokenStatus.awaits);
        tokenRepo.save(token);
        String otpToken = jwtUtils.OtpToken(email, user.getUserId(), uuid.toString());
        String link = baseUrl + "/reset-password?token=" + otpToken;
        emailService.sendResetLinkEmail(email, link);
        return RestApiResponse.success();
    }

    public RestApiResponse resetPassword(String token, String newPassword) {
        Map<String, String> tokenDetails = jwtUtils.getFromOtpToken(token);
        UUID uuid = UUID.fromString(tokenDetails.get("uuid"));
        ValidationToken validationToken = tokenRepo.findByUuid(uuid);
        if(validateToken(validationToken, uuid)) {
            throw new BadRequestException("Token already used or invalid");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }
        User user = userRepo.findByEmail(tokenDetails.get("email"));
        if(user == null) {
            throw new BadRequestException("User not found");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        validationToken.setStatus(ValidationTokenStatus.Consumed);
        tokenRepo.save(validationToken);

        return RestApiResponse.success();
    }

    private boolean validateToken(ValidationToken validationToken, UUID uuid) {
        if(validationToken == null || !validationToken.getStatus().equals(ValidationTokenStatus.awaits)) {
            throw new BadRequestException("Token already used or invalid");
        }
        Instant expiryDuration = Instant.now().minus(10, ChronoUnit.MINUTES);
        if(!validationToken.getCreatedAt().isAfter(expiryDuration)) {
            validationToken.setStatus(ValidationTokenStatus.Expired);
            tokenRepo.save(validationToken);
            throw new BadRequestException("Token is expired.");
        }

        return validationToken.getUuid().equals(uuid);
    }

    // requires redis
//    public void logout(String token);
//    public AuthResponse refreshToken(String refreshToken);
}
