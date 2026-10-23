
package com.spring.springSecurity.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expireInSeconds
) {
}
