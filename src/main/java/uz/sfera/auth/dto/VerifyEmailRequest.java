package uz.sfera.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequest(
        @NotBlank(message = "Email kiritilishi shart")
        @Email(message = "Email formati noto'g'ri")
        String email,

        @NotBlank(message = "Tasdiqlash kodi kiritilishi shart")
        @Pattern(regexp = "\\d{6}", message = "Tasdiqlash kodi 6 ta raqamdan iborat bo'lishi kerak")
        String code
) {
}
