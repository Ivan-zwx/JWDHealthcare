package hr.algebra.jwdhealthcare.dto.api.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * API login request data is transferred from REST clients to authentication endpoints.
 */
@Getter
@Setter
@NoArgsConstructor
public class ApiLoginRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}