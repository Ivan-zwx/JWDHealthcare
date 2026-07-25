package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    /**
     * Finds all user accounts for administrator display.
     *
     * @return user accounts ordered by role and full name
     */
    @Query("""
            select u
            from UserAccount u
            order by u.role asc, u.fullName asc
            """)
    List<UserAccount> findAllForAdminList();

    /**
     * Finds one user account for administrator management.
     *
     * @param idUserAccount the user account identifier
     * @return the user account if it exists
     */
    @Query("""
            select u
            from UserAccount u
            where u.idUserAccount = :idUserAccount
            """)
    Optional<UserAccount> findByIdForAdmin(@Param("idUserAccount") Integer idUserAccount);
}