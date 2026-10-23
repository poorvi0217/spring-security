package com.spring.springSecurity.controller;

import com.spring.springSecurity.dto.LoginRequest;
import com.spring.springSecurity.dto.TokenResponse;
import com.spring.springSecurity.repo.AppUserRepository;
import com.spring.springSecurity.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository repository;
    private  final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final JwtDecoder jwtDecoder;

    public AuthController(AppUserRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService, JwtDecoder jwtDecoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.jwtDecoder = jwtDecoder;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request)
    {
        Authentication authentication=authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.
                        unauthenticated(request.email().toLowerCase(),request.password()));
        return new TokenResponse(
                tokenService.create(authentication),
                tokenService.createRefreshToken(authentication),
                "Bearer",
                tokenService.expireInSeconds()
        );

    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody RefreshRequest request) {

        try {
            Jwt jwt = jwtDecoder.decode(request.refreshToken());

            if (!"refresh".equals(
                    jwt.getClaimAsString("token_type"))) {
                return ResponseEntity.status(401)
                        .body(Map.of("error",
                                "A refresh token is required"));
            }

            List<String> roles =
                    jwt.getClaimAsStringList("roles");

            if (roles == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Invalid token roles"));
            }

            var authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(
                            "ROLE_" + role))
                    .toList();

            var authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            jwt.getSubject(),
                            null,
                            authorities
                    );

            return ResponseEntity.ok(Map.of(
                    "accessToken",
                    tokenService.create(authentication),
                    "tokenType",
                    "Bearer",
                    "expireInSeconds",
                    tokenService.expireInSeconds()
            ));

        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "error",
                            "Invalid or expired refresh token"
                    ));
        }
    }


    public record RefreshRequest(String refreshToken) {}

}
