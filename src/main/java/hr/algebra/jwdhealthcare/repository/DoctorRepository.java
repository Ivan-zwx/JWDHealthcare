package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access operations are provided for doctor profiles.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
}