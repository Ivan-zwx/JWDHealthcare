package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access operations are provided for medical records.
 */
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
}