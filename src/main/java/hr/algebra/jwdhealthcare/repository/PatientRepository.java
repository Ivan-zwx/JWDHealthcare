package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
}