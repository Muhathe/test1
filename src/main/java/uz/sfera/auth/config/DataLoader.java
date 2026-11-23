package uz.sfera.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.sfera.auth.entity.AppUser;
import uz.sfera.auth.entity.Role;
import uz.sfera.auth.repository.AppUserRepository;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.email}")
    private String adminEmail;

    @Value("${app.seed.admin.password}")
    private String adminPassword;

    @Value("${app.seed.admin.first-name:System}")
    private String adminFirstName;

    @Value("${app.seed.admin.last-name:Admin}")
    private String adminLastName;

    @Bean
    public CommandLineRunner loadInitialData() {
        return args -> {
            if (appUserRepository.existsByEmailIgnoreCase(adminEmail)) {
                log.info("DataLoader: admin account already exists: {}", adminEmail);
                return;
            }

            AppUser admin = new AppUser();
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setEmail(adminEmail.toLowerCase());
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            admin.setEmailVerified(true);
            appUserRepository.save(admin);

            log.warn("DataLoader created ADMIN account '{}'. Change the demo password before deployment.", adminEmail);
        };
    }
}
