package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access operations are provided for user accounts.
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
}