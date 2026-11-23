package uz.sfera.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import uz.sfera.auth.dto.AuthResponse;
import uz.sfera.auth.dto.LoginRequest;
import uz.sfera.auth.dto.MessageResponse;
import uz.sfera.auth.dto.RegisterRequest;
import uz.sfera.auth.dto.UserResponse;
import uz.sfera.auth.entity.AppUser;
import uz.sfera.auth.entity.EmailVerificationToken;
import uz.sfera.auth.entity.Role;
import uz.sfera.auth.repository.AppUserRepository;
import uz.sfera.auth.repository.EmailVerificationTokenRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AppUserRepository appUserRepository;
    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${app.verification.expiration-minutes:10}")
    private long verificationExpirationMinutes;

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (appUserRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu email bilan akkaunt mavjud");
        }

        AppUser user = new AppUser();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setEmailVerified(false);
        appUserRepository.save(user);

        sendNewVerificationCode(user);
        return new MessageResponse("Ro'yxatdan o'tdingiz. Email manzilingizga yuborilgan 6 xonali kod bilan tasdiqlang.");
    }

    @Transactional
    public MessageResponse verifyEmail(String emailInput, String code) {
        AppUser user = findUser(emailInput);
        if (user.isEmailVerified()) {
            return new MessageResponse("Email avval tasdiqlangan");
        }

        EmailVerificationToken verificationToken = verificationTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tasdiqlash kodi topilmadi. Kodni qayta yuboring"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tasdiqlash kodi muddati tugagan");
        }

        if (!passwordEncoder.matches(code, verificationToken.getTokenHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tasdiqlash kodi noto'g'ri");
        }

        user.setEmailVerified(true);
        appUserRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return new MessageResponse("Email muvaffaqiyatli tasdiqlandi. Endi login qilishingiz mumkin.");
    }

    @Transactional
    public MessageResponse resendVerificationCode(String emailInput) {
        AppUser user = findUser(emailInput);
        if (user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email allaqachon tasdiqlangan");
        }
        sendNewVerificationCode(user);
        return new MessageResponse("Yangi tasdiqlash kodi emailingizga yuborildi");
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        AppUser user = (AppUser) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs(), UserResponse.from(user));
    }

    private void sendNewVerificationCode(AppUser user) {
        String code = String.format(Locale.ROOT, "%06d", SECURE_RANDOM.nextInt(1_000_000));

        verificationTokenRepository.deleteByUserId(user.getId());
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(passwordEncoder.encode(code));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(verificationExpirationMinutes));
        verificationTokenRepository.save(token);

        emailService.sendVerificationCode(user.getEmail(), code, verificationExpirationMinutes);
    }

    private AppUser findUser(String emailInput) {
        return appUserRepository.findByEmailIgnoreCase(normalizeEmail(emailInput))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foydalanuvchi topilmadi"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
