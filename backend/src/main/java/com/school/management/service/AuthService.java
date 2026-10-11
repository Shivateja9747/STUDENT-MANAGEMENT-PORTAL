package com.school.management.service;

import com.school.management.dto.JwtAuthResponse;
import com.school.management.dto.LoginRequest;
import com.school.management.entity.Role;
import com.school.management.entity.Student;
import com.school.management.entity.Teacher;
import com.school.management.entity.User;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.TeacherRepository;
import com.school.management.repository.UserRepository;
import com.school.management.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       StudentRepository studentRepository,
                       TeacherRepository teacherRepository,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public JwtAuthResponse login(LoginRequest loginRequest) {
        String inputIdentifier = loginRequest.getUsername() != null ? loginRequest.getUsername().trim() : "";
        Optional<User> userOpt = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(inputIdentifier, inputIdentifier);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getLockoutUntil() != null && LocalDateTime.now().isBefore(user.getLockoutUntil())) {
                long minutesLeft = Duration.between(LocalDateTime.now(), user.getLockoutUntil()).toMinutes() + 1;
                throw new RuntimeException("Account locked due to 5 failed login attempts. Please try again after " + minutesLeft + " minutes.");
            }
        }

        String actualUsername = userOpt.isPresent() ? userOpt.get().getUsername() : inputIdentifier;

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            actualUsername,
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(actualUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + actualUsername));

            // Reset failed login attempts on successful login
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            userRepository.save(user);

            String name = user.getUsername();
            if (user.getRole() == Role.TEACHER) {
                Optional<Teacher> teacherOpt = teacherRepository.findByUserId(user.getId());
                if (teacherOpt.isPresent()) {
                    name = teacherOpt.get().getName();
                }
            } else if (user.getRole() == Role.STUDENT) {
                Optional<Student> studentOpt = studentRepository.findByUserId(user.getId());
                if (studentOpt.isPresent()) {
                    name = studentOpt.get().getName();
                }
            } else if (user.getRole() == Role.ADMIN) {
                name = "System Administrator";
            }

            return JwtAuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(name)
                    .role(user.getRole())
                    .mustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()))
                    .build();
        } catch (Exception e) {
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                int attempts = (user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0) + 1;
                user.setFailedLoginAttempts(attempts);
                if (attempts >= 5) {
                    user.setLockoutUntil(LocalDateTime.now().plusMinutes(15));
                }
                userRepository.save(user);
            }
            if (e instanceof BadCredentialsException) {
                throw (BadCredentialsException) e;
            }
            throw new BadCredentialsException(e.getMessage() != null && e.getMessage().contains("locked") ? e.getMessage() : "Invalid username or password.", e);
        }
    }
}
