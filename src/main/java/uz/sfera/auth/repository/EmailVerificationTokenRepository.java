package uz.sfera.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sfera.auth.entity.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
