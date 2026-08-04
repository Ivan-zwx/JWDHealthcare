package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import hr.algebra.jwdhealthcare.dto.api.auth.ApiCurrentUserDto;
import hr.algebra.jwdhealthcare.dto.api.auth.ApiErrorResponseDto;
import hr.algebra.jwdhealthcare.dto.api.auth.ApiLoginRequestDto;
import hr.algebra.jwdhealthcare.dto.api.auth.ApiLoginResponseDto;
import hr.algebra.jwdhealthcare.repository.UserAccountRepository;
import hr.algebra.jwdhealthcare.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API authentication endpoints are handled.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;

    /**
     * Authenticates API credentials and returns a JWT access token.
     *
     * @param requestDto the submitted API login request
     * @return the login response containing the generated JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ApiLoginRequestDto requestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponseDto("Invalid username or password."));
        }

        UserAccount userAccount = userAccountRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User account was not found."));

        String token = jwtService.generateToken(userAccount);

        return ResponseEntity.ok(new ApiLoginResponseDto(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                userAccount.getUsername(),
                userAccount.getRole().name()
        ));
    }

    /**
     * Displays the authenticated API user's token identity.
     *
     * @param authentication the JWT authentication token
     * @return the current API user data
     */
    @GetMapping("/me")
    public ApiCurrentUserDto me(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        return new ApiCurrentUserDto(
                jwt.getSubject(),
                jwt.getClaimAsString("role"),
                authorities
        );
    }
}