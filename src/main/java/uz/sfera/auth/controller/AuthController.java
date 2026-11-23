package uz.sfera.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.sfera.auth.dto.*;
import uz.sfera.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, email verification and login API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register", description = "Emailga 6 xonali tasdiqlash kodi yuboradi")
    public MessageResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Emailga kelgan OTP kodni tasdiqlaydi")
    public MessageResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return authService.verifyEmail(request.email(), request.code());
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification code")
    public MessageResponse resendVerification(@Valid @RequestBody ResendCodeRequest request) {
        return authService.resendVerificationCode(request.email());
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Tasdiqlangan email va parol orqali JWT token oladi")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
