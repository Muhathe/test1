package uz.sfera.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Ism kiritilishi shart")
        @Size(max = 60, message = "Ism 60 ta belgidan oshmasin")
        String firstName,

        @NotBlank(message = "Familiya kiritilishi shart")
        @Size(max = 60, message = "Familiya 60 ta belgidan oshmasin")
        String lastName,

        @NotBlank(message = "Email kiritilishi shart")
        @Email(message = "Email formati noto'g'ri")
        @Size(max = 180)
        String email,

        @NotBlank(message = "Parol kiritilishi shart")
        @Size(min = 8, max = 72, message = "Parol 8-72 ta belgidan iborat bo'lishi kerak")
        String password
) {
}
