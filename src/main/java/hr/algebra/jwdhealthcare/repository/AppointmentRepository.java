package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access operations are provided for appointments.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
}