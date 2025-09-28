package com.tsystem.service;


import com.tsystem.model.user.User;

import com.tsystem.model.dto.*;
import com.tsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

//    /** Registration */
//    public TokenResponse register(RegisterRequest request) {
//        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
//            throw new IllegalArgumentException("User with same username or email already exists");
//        }
//
//        User user = User.builder()
//                .username(request.getUsername())
//                .email(request.getEmail())
//                .name(request.getName())         // важно, если NOT NULL
//                .surname(request.getSurname())   // важно, если NOT NULL
//                .password(passwordEncoder.encode(request.getPassword()))
//                // .role(SystemRole.SYSTEM_USER)
//                .build();
//
//        userRepository.save(user);
//        String jwtToken = jwtService.generateToken((UserDetails) user);
//
//        return new TokenResponse(jwtToken);
//    }

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new IllegalArgumentException("User with same username or email already exists");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .name(request.getName())         // важно, если NOT NULL
                .surname(request.getSurname())   // важно, если NOT NULL
                .password(passwordEncoder.encode(request.getPassword()))
                // .role(SystemRole.SYSTEM_USER)
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return new TokenResponse(jwtToken);
    }


    /**
     * Authentication. login = username OR email.
     * AuthenticationManager expects a username, so when logging in by email,
     * we first find the user and substitute their username.
     */
    public TokenResponse authenticate(LoginRequest request) {
        var user = userRepository.findByUsername(request.getLogin())
                .orElseGet(() -> userRepository.findByEmail(request.getLogin())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials")));

        // проверка пароля и создание SecurityContext — через AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        String jwtToken = jwtService.generateToken((UserDetails) user);
        return new TokenResponse(jwtToken);
    }
}