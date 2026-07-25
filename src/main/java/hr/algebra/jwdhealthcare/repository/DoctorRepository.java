package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Data access operations are provided for doctor profiles.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    /**
     * Finds all doctors with their linked user accounts.
     *
     * @return doctors ordered by full name
     */
    @Query("""
            select d
            from Doctor d
            join fetch d.userAccount u
            order by u.fullName asc
            """)
    List<Doctor> findAllWithUserAccount();

    /**
     * Finds a doctor profile by the linked user account username.
     *
     * @param username the authenticated username
     * @return the doctor profile if it exists
     */
    @Query("""
            select d
            from Doctor d
            join fetch d.userAccount u
            where u.username = :username
            """)
    Optional<Doctor> findByUsername(@Param("username") String username);
}