package uz.sfera.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email kiritilishi shart")
        @Email(message = "Email formati noto'g'ri")
        String email,

        @NotBlank(message = "Parol kiritilishi shart")
        String password
) {
}
