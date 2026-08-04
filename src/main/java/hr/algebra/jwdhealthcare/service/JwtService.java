package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * JWT access tokens are generated for REST API authentication.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.expiration-seconds}")
    private long expirationSeconds;

    /**
     * Generates a signed JWT access token for a user account.
     *
     * @param userAccount the authenticated user account
     * @return the generated JWT value
     */
    public String generateToken(UserAccount userAccount) {
        Instant now = Instant.now();
        String authority = "ROLE_" + userAccount.getRole().name();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .subject(userAccount.getUsername())
                .claim("role", userAccount.getRole().name())
                .claim("authorities", List.of(authority))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /**
     * Gets the configured JWT expiration duration.
     *
     * @return token expiration duration in seconds
     */
    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}