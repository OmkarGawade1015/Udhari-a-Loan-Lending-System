package com.sprint.lending.security;

import com.sprint.lending.entity.User;
import com.sprint.lending.entity.UserRole;
import com.sprint.lending.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminBootstrapRunner(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${UDHARI_ADMIN_EMAIL:}") String adminEmail,
            @Value("${UDHARI_ADMIN_PASSWORD:}") String adminPassword
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean emailProvided = adminEmail != null && !adminEmail.isBlank();
        boolean passwordProvided = adminPassword != null && !adminPassword.isBlank();

        if (!emailProvided && !passwordProvided) {
            return;
        }
        if (!emailProvided || !passwordProvided) {
            throw new IllegalStateException(
                    "Set both UDHARI_ADMIN_EMAIL and UDHARI_ADMIN_PASSWORD to bootstrap an admin."
            );
        }
        if (adminPassword.length() < 12) {
            throw new IllegalStateException("UDHARI_ADMIN_PASSWORD must be at least 12 characters.");
        }
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("The configured admin email is already used by another account.");
        }

        userRepository.save(new User(
                normalizedEmail,
                passwordEncoder.encode(adminPassword),
                UserRole.ADMIN
        ));
    }
}
