package uz.sfera.auth.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.sfera.auth.dto.UserResponse;
import uz.sfera.auth.entity.AppUser;
import uz.sfera.auth.repository.AppUserRepository;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Users", description = "JWT bilan himoyalangan user endpointlari")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final AppUserRepository appUserRepository;

    @GetMapping("/users/me")
    public UserResponse getMyProfile(@AuthenticationPrincipal AppUser currentUser) {
        return UserResponse.from(currentUser);
    }

    @GetMapping("/admin/users")
    public List<UserResponse> getAllUsers() {
        return appUserRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }
}
