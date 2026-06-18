package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access operations are provided for patient profiles.
 */
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}