package hr.algebra.jwdhealthcare.security;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import hr.algebra.jwdhealthcare.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User account data is adapted for Spring Security authentication.
 */
@Service
@RequiredArgsConstructor
public class HealthcareUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    /**
     * Loads a user account by username for Spring Security authentication.
     *
     * @param username the submitted username
     * @return user details used by Spring Security
     * @throws UsernameNotFoundException if the username does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User account was not found."));

        return new User(
                userAccount.getUsername(),
                userAccount.getPasswordHash(),
                userAccount.isEnabled(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name()))
        );
    }
}