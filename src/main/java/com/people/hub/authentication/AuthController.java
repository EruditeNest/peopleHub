package com.people.hub.authentication;

import com.people.hub.authentication.dto.ChangePasswordDTO;
import com.people.hub.authentication.dto.LoginDTO;
import com.people.hub.common.RestApiResponse;
import com.people.hub.security.MyUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<RestApiResponse> login(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.loginGenerateToken(loginDTO.getEmail(), loginDTO.getPassword()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<RestApiResponse> changePassword(
            @RequestBody ChangePasswordDTO passwordDTO,
            @AuthenticationPrincipal MyUserDetail userDetails) {
        return ResponseEntity.ok(authService.changePassword(passwordDTO.getOldPassword(), passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword(), userDetails));
    }

    @GetMapping("/forget-password")
    public ResponseEntity<RestApiResponse> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<RestApiResponse> resetPassword(@RequestParam String token, @RequestBody String newPassword) {
        return ResponseEntity.ok(authService.resetPassword(token, newPassword));
    }

    @PostMapping("/renew-token")
    public ResponseEntity<RestApiResponse> renewTokens(@RequestBody String token) {
        return ResponseEntity.ok(authService.renewTokens(token));
    }

    @GetMapping("/logout")
    public ResponseEntity<RestApiResponse> logout(@AuthenticationPrincipal MyUserDetail userDetail) {
        return ResponseEntity.ok(authService.revokeToken(userDetail));
    }
}
