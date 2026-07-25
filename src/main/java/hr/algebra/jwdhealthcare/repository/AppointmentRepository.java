package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data access operations are provided for appointments.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    /**
     * Finds all appointments with data needed for the administrator appointment list.
     *
     * @return appointments ordered by scheduled time
     */
    @Query("""
            select a
            from Appointment a
            join fetch a.doctor d
            join fetch d.userAccount
            join fetch a.patient p
            join fetch p.userAccount
            order by a.scheduledAt asc
            """)
    List<Appointment> findAllForAdminList();

    /**
     * Finds one appointment with data needed for editing and display.
     *
     * @param idAppointment the appointment identifier
     * @return the appointment if it exists
     */
    @Query("""
            select a
            from Appointment a
            join fetch a.doctor d
            join fetch d.userAccount
            join fetch a.patient p
            join fetch p.userAccount
            where a.idAppointment = :idAppointment
            """)
    Optional<Appointment> findByIdForAdmin(@Param("idAppointment") Integer idAppointment);

    /**
     * Finds all appointments for a patient with data needed for patient display.
     *
     * @param patientId the patient identifier
     * @return patient appointments ordered by scheduled time
     */
    @Query("""
            select a
            from Appointment a
            join fetch a.doctor d
            join fetch d.userAccount
            join a.patient p
            where p.idPatient = :patientId
            order by a.scheduledAt asc
            """)
    List<Appointment> findAllForPatientView(@Param("patientId") Integer patientId);

    /**
     * Finds all appointments for a doctor with data needed for doctor schedule display.
     *
     * @param doctorId the doctor identifier
     * @return doctor appointments ordered by scheduled time
     */
    @Query("""
            select a
            from Appointment a
            join fetch a.patient p
            join fetch p.userAccount
            join a.doctor d
            where d.idDoctor = :doctorId
            order by a.scheduledAt asc
            """)
    List<Appointment> findAllForDoctorSchedule(@Param("doctorId") Integer doctorId);

    /**
     * Finds one appointment assigned to a doctor with data needed for medical record handling.
     *
     * @param idAppointment the appointment identifier
     * @param doctorId the doctor identifier
     * @return the appointment if it belongs to the doctor
     */
    @Query("""
            select a
            from Appointment a
            join fetch a.doctor d
            join fetch d.userAccount
            join fetch a.patient p
            join fetch p.userAccount
            where a.idAppointment = :idAppointment
              and d.idDoctor = :doctorId
            """)
    Optional<Appointment> findByIdForDoctor(
            @Param("idAppointment") Integer idAppointment,
            @Param("doctorId") Integer doctorId
    );

    /**
     * Checks whether a doctor already has an active appointment in the selected time slot.
     *
     * @param doctorId the doctor identifier
     * @param scheduledAt the selected appointment slot
     * @param cancelledStatus the appointment status that does not block scheduling
     * @return true if the doctor already has an active appointment in the slot
     */
    @Query("""
            select case when count(a) > 0 then true else false end
            from Appointment a
            where a.doctor.idDoctor = :doctorId
              and a.scheduledAt = :scheduledAt
              and a.status <> :cancelledStatus
            """)
    boolean doctorHasActiveAppointmentAt(
            @Param("doctorId") Integer doctorId,
            @Param("scheduledAt") LocalDateTime scheduledAt,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    /**
     * Checks whether a doctor already has another active appointment in the selected time slot.
     *
     * @param doctorId the doctor identifier
     * @param scheduledAt the selected appointment slot
     * @param excludedAppointmentId the appointment that should be ignored during edit checks
     * @param cancelledStatus the appointment status that does not block scheduling
     * @return true if another active doctor appointment exists in the slot
     */
    @Query("""
            select case when count(a) > 0 then true else false end
            from Appointment a
            where a.doctor.idDoctor = :doctorId
              and a.scheduledAt = :scheduledAt
              and a.idAppointment <> :excludedAppointmentId
              and a.status <> :cancelledStatus
            """)
    boolean doctorHasAnotherActiveAppointmentAt(
            @Param("doctorId") Integer doctorId,
            @Param("scheduledAt") LocalDateTime scheduledAt,
            @Param("excludedAppointmentId") Integer excludedAppointmentId,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    /**
     * Checks whether a patient already has an active appointment in the selected time slot.
     *
     * @param patientId the patient identifier
     * @param scheduledAt the selected appointment slot
     * @param cancelledStatus the appointment status that does not block scheduling
     * @return true if the patient already has an active appointment in the slot
     */
    @Query("""
            select case when count(a) > 0 then true else false end
            from Appointment a
            where a.patient.idPatient = :patientId
              and a.scheduledAt = :scheduledAt
              and a.status <> :cancelledStatus
            """)
    boolean patientHasActiveAppointmentAt(
            @Param("patientId") Integer patientId,
            @Param("scheduledAt") LocalDateTime scheduledAt,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    /**
     * Checks whether a patient already has another active appointment in the selected time slot.
     *
     * @param patientId the patient identifier
     * @param scheduledAt the selected appointment slot
     * @param excludedAppointmentId the appointment that should be ignored during edit checks
     * @param cancelledStatus the appointment status that does not block scheduling
     * @return true if another active patient appointment exists in the slot
     */
    @Query("""
            select case when count(a) > 0 then true else false end
            from Appointment a
            where a.patient.idPatient = :patientId
              and a.scheduledAt = :scheduledAt
              and a.idAppointment <> :excludedAppointmentId
              and a.status <> :cancelledStatus
            """)
    boolean patientHasAnotherActiveAppointmentAt(
            @Param("patientId") Integer patientId,
            @Param("scheduledAt") LocalDateTime scheduledAt,
            @Param("excludedAppointmentId") Integer excludedAppointmentId,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );
}