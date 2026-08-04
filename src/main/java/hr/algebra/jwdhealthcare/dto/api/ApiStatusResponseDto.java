package hr.algebra.jwdhealthcare.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API status response data is transferred from secured role endpoints to REST clients.
 */
@Getter
@AllArgsConstructor
public class ApiStatusResponseDto {

    private final String area;
    private final String username;
    private final String message;
}