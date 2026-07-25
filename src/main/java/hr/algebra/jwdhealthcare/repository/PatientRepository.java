package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Data access operations are provided for patient profiles.
 */
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    /**
     * Finds all patients with their linked user accounts.
     *
     * @return patients ordered by full name
     */
    @Query("""
            select p
            from Patient p
            join fetch p.userAccount u
            order by u.fullName asc
            """)
    List<Patient> findAllWithUserAccount();

    /**
     * Finds a patient profile by the linked user account username.
     *
     * @param username the authenticated username
     * @return the patient profile if it exists
     */
    @Query("""
            select p
            from Patient p
            join fetch p.userAccount u
            where u.username = :username
            """)
    Optional<Patient> findByUsername(@Param("username") String username);
}