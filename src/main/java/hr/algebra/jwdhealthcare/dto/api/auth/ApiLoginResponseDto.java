package hr.algebra.jwdhealthcare.dto.api.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API login response data is transferred from authentication endpoints to REST clients.
 */
@Getter
@AllArgsConstructor
public class ApiLoginResponseDto {

    private final String token;
    private final String tokenType;
    private final long expiresInSeconds;
    private final String username;
    private final String role;
}