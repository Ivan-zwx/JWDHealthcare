package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Data access operations are provided for user accounts.
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {

    /**
     * Finds a user account by username.
     *
     * @param username the username used for authentication
     * @return the user account if it exists
     */
    @Query("""
            select u
            from UserAccount u
            where u.username = :username
            """)
    Optional<UserAccount> findByUsername(@Param("username") String username);
}