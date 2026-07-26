package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import hr.algebra.jwdhealthcare.domain.Patient;
import hr.algebra.jwdhealthcare.dto.view.PatientReminderViewDto;
import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Appointment reminder business operations are coordinated between scheduled jobs, MVC controllers, and repositories.
 */
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Value("${app.reminders.look-ahead-hours}")
    private int reminderLookAheadHours;

    /**
     * Generates reminders for upcoming scheduled appointments.
     *
     * @return the number of reminders generated
     */
    @Transactional
    public int generateAppointmentReminders() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalDateTime reminderUntil = now.plusHours(reminderLookAheadHours);

        List<Appointment> appointments = appointmentRepository.findUpcomingAppointmentsNeedingReminder(
                now,
                reminderUntil,
                AppointmentStatus.SCHEDULED
        );

        for (Appointment appointment : appointments) {
            appointment.setReminderGeneratedAt(now);
        }

        appointmentRepository.saveAll(appointments);

        return appointments.size();
    }

    /**
     * Finds generated reminders for the authenticated patient.
     *
     * @param username the authenticated username
     * @return patient reminder view data
     */
    @Transactional(readOnly = true)
    public List<PatientReminderViewDto> findRemindersForPatient(String username) {
        Patient patient = findPatientByUsername(username);

        return appointmentRepository.findGeneratedRemindersForPatient(
                        patient.getIdPatient(),
                        AppointmentStatus.SCHEDULED
                )
                .stream()
                .map(this::toPatientReminderViewDto)
                .toList();
    }

    private Patient findPatientByUsername(String username) {
        return patientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile was not found."));
    }

    private PatientReminderViewDto toPatientReminderViewDto(Appointment appointment) {
        return new PatientReminderViewDto(
                appointment.getIdAppointment(),
                appointment.getDoctor().getUserAccount().getFullName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getReason(),
                appointment.getScheduledAt(),
                appointment.getReminderGeneratedAt()
        );
    }
}