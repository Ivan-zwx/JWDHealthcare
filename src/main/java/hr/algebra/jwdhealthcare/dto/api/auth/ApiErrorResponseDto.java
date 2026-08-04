package hr.algebra.jwdhealthcare.dto.api.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API error response data is transferred from REST endpoints to clients.
 */
@Getter
@AllArgsConstructor
public class ApiErrorResponseDto {

    private final String message;
}