package com.people.hub.authentication;

import com.people.hub.authentication.token.*;
import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.BadRequestException;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.user.model.User;
import com.people.hub.user.repository.UserRepo;
import com.people.hub.security.JwtUtils;
import com.people.hub.security.MyUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${WEBSITE_BASEURL}")
    private String baseUrl;
    @Value("${REFRESH_TOKEN_EXPIRY}")
    private Long REFRESH_TOKEN_EXPIRY;

    private final UserRepo userRepo;
    private final ValidationTokenRepo tokenRepo;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepo refreshTokenRepo;

    @Transactional
    public RestApiResponse loginGenerateToken(String email, String password) {
        try{
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User", email));
            String userPassword = user.getPassword();
            if(passwordEncoder.matches(password, userPassword)) {
                revokeAllRefreshTokenByUserId(user.getUserId());
                String refreshToken = createRefreshToken(user.getUserId()).toString();
                String token = jwtUtils.createToken(email, user.getUserId());
                return RestApiResponse.responseJwtRefreshToken(token, refreshToken);
            }
            return RestApiResponse.failure("Incorrect Password");
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Transactional
    public RestApiResponse changePassword(String oldPassword, String newPassword, String confirmPassword, MyUserDetail userDetails) {

        User user = userRepo.findById(userDetails.getUserId())
                .orElseThrow(() -> new NotFoundException("User", userDetails.getUserId()));

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
        int revokedTokens = revokeAllRefreshTokenByUserId(user.getUserId());

        userRepo.save(user);

        return RestApiResponse.success(revokedTokens);
    }

    public RestApiResponse forgotPassword(String email) {
        try{
            User user = userRepo.findByEmail(email)
                    .orElseThrow();
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
        } catch(Exception e) {
            log.info("ForgetPassword: {}", e.getMessage());
            return RestApiResponse.success();
        }
    }

    @Transactional
    public RestApiResponse resetPassword(String token, String newPassword) {
        Map<String, String> tokenDetails = jwtUtils.getFromOtpToken(token);
        UUID uuid = UUID.fromString(tokenDetails.get("uuid"));
        ValidationToken validationToken = tokenRepo.findByUuid(uuid);
        validateToken(validationToken, uuid);
        if (newPassword == null || newPassword.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }
        User user = userRepo.findByEmail(tokenDetails.get("email"))
            .orElseThrow(() -> new NotFoundException("User", tokenDetails.get("email")));
        user.setPassword(passwordEncoder.encode(newPassword));
        revokeAllRefreshTokenByUserId(user.getUserId());
        userRepo.save(user);
        validationToken.setStatus(ValidationTokenStatus.Consumed);
        tokenRepo.save(validationToken);

        return RestApiResponse.success();
    }

    private UUID createRefreshToken(Long userId) {
        UUID uuid = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(uuid);
        refreshToken.setExpiryDate(
                new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY)
        );
        refreshToken.setRevoked(false);
        refreshTokenRepo.save(refreshToken);
        return uuid;
    }

    @Transactional
    public RestApiResponse renewTokens(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(UUID.fromString(token))
                .orElseThrow(() -> new BadRequestException("invalid refresh token, Login again!!"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().before(new Date())) {
            throw new BadRequestException("Refresh token expired. Login again!!");
        }
        //Since method is transactional and entity is managed, we can just set revoke and not use save method.
        refreshToken.setRevoked(true);

        String newRefreshToken = createRefreshToken(refreshToken.getUserId()).toString();

        User user = userRepo.findById(refreshToken.getUserId())
                .orElseThrow();
        String newJwtToken = jwtUtils.createToken(user.getEmail(), user.getUserId());
        return RestApiResponse.responseJwtRefreshToken(newJwtToken, newRefreshToken);
    }

    public RestApiResponse revokeToken(MyUserDetail userDetails) {
        int revokedTokens = revokeAllRefreshTokenByUserId(userDetails.getUserId());
        return RestApiResponse.success(revokedTokens);
    }

    private int revokeAllRefreshTokenByUserId(Long userId) {
        // Implement: remove the permissions of user from redis.
        return refreshTokenRepo.revokeActiveTokensByUserId(userId);
    }

    private void validateToken(ValidationToken validationToken, UUID uuid) {
        if(validationToken == null || !validationToken.getStatus().equals(ValidationTokenStatus.awaits)) {
            throw new BadRequestException("Token already used or invalid");
        }
        Instant expiryDuration = Instant.now().minus(10, ChronoUnit.MINUTES);
        if(!validationToken.getCreatedAt().isAfter(expiryDuration)) {
            validationToken.setStatus(ValidationTokenStatus.Expired);
            tokenRepo.save(validationToken);
            throw new BadRequestException("Token is expired.");
        }

        if(!validationToken.getUuid().equals(uuid)) {
            throw new BadRequestException("Token is invalid.");
        }
    }
}
