package com.sprint.lending.service;

import com.sprint.lending.dto.AuthResponse;
import com.sprint.lending.dto.LoginRequest;
import com.sprint.lending.dto.RegistrationRequest;
import com.sprint.lending.dto.RegistrationResponse;
import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.User;
import com.sprint.lending.entity.UserRole;
import com.sprint.lending.exception.AccountAlreadyExistsException;
import com.sprint.lending.repository.BorrowerProfileRepository;
import com.sprint.lending.repository.UserRepository;
import com.sprint.lending.security.JwtTokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            UserRepository userRepository,
            BorrowerProfileRepository borrowerProfileRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.userRepository = userRepository;
        this.borrowerProfileRepository = borrowerProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new AccountAlreadyExistsException("An account with this email already exists.");
        }

        User user = userRepository.save(new User(
                email,
                passwordEncoder.encode(request.password()),
                UserRole.BORROWER
        ));

        BorrowerProfile profile = borrowerProfileRepository.save(new BorrowerProfile(
                request.fullName().trim(),
                request.monthlyIncome(),
                request.employmentType().trim(),
                user
        ));

        return new RegistrationResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                profile.getFullName(),
                profile.getMonthlyIncome(),
                profile.getEmploymentType()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        return new AuthResponse(
                jwtTokenService.issueToken(user),
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
