package uz.sfera.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sfera.auth.entity.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
