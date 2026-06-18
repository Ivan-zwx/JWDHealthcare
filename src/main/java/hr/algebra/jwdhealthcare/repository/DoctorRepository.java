package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
}