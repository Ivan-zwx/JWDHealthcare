package hr.algebra.jwdhealthcare.dto.api.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Current API user data is transferred from secured REST endpoints to clients.
 */
@Getter
@AllArgsConstructor
public class ApiCurrentUserDto {

    private final String username;
    private final String role;
    private final List<String> authorities;
}