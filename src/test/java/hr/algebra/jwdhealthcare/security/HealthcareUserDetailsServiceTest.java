package hr.algebra.jwdhealthcare.security;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import hr.algebra.jwdhealthcare.domain.UserRole;
import hr.algebra.jwdhealthcare.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthcareUserDetailsServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private HealthcareUserDetailsService healthcareUserDetailsService;

    @Test
    void loadUserByUsernameReturnsUserDetailsWithRoleAuthority() {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("doctor");
        userAccount.setPasswordHash("password-hash");
        userAccount.setRole(UserRole.DOCTOR);
        userAccount.setEnabled(true);

        when(userAccountRepository.findByUsername("doctor")).thenReturn(Optional.of(userAccount));

        UserDetails userDetails = healthcareUserDetailsService.loadUserByUsername("doctor");

        assertEquals("doctor", userDetails.getUsername());
        assertEquals("password-hash", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_DOCTOR")));
    }

    @Test
    void loadUserByUsernameThrowsForMissingUser() {
        when(userAccountRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> healthcareUserDetailsService.loadUserByUsername("missing")
        );
    }
}