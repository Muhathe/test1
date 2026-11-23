package uz.sfera.auth.dto;

import uz.sfera.auth.entity.AppUser;
import uz.sfera.auth.entity.Role;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        boolean emailVerified
) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isEmailVerified()
        );
    }
}
