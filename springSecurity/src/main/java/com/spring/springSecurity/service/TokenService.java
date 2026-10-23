package com.spring.springSecurity.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService {

    private  final JwtEncoder encoder;

    private final Duration lifeTime = Duration.ofMinutes(10);

    private final Duration refreshTokenLifeTime = Duration.ofDays(7);

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public long expireInSeconds(){
        return lifeTime.getSeconds();
    }

    public String createRefreshToken(Authentication authentication){
        return generateToken(
                authentication , "refresh" , refreshTokenLifeTime
        );
    }

    public String generateToken(Authentication authentication , String tokenType , Duration refreshTokenLifeTime){

        Instant now = Instant.now();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(v->v.replaceFirst("^ROLE_",""))
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cfs-security")
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plus(lifeTime))
                .id(UUID.randomUUID().toString())
                .claim("roles",roles)
                .claim("token_type",tokenType)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    public String create(Authentication authentication){
        return generateToken(authentication , "access" , lifeTime);
    }
}
