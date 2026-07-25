package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Data access operations are provided for medical records.
 */
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {

    /**
     * Finds a medical record by appointment identifier.
     *
     * @param idAppointment the appointment identifier
     * @return the medical record if it exists
     */
    @Query("""
            select mr
            from MedicalRecord mr
            join fetch mr.appointment a
            where a.idAppointment = :idAppointment
            """)
    Optional<MedicalRecord> findByAppointmentId(@Param("idAppointment") Integer idAppointment);
}