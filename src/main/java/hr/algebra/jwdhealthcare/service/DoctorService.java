package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.Doctor;
import hr.algebra.jwdhealthcare.dto.view.DoctorAppointmentViewDto;
import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Doctor business operations are coordinated between MVC controllers and repositories.
 */
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Finds schedule appointments for the authenticated doctor.
     *
     * @param username the authenticated username
     * @return doctor appointment view data
     */
    @Transactional(readOnly = true)
    public List<DoctorAppointmentViewDto> findScheduleForDoctor(String username) {
        Doctor doctor = findDoctorByUsername(username);

        return appointmentRepository.findAllForDoctorSchedule(doctor.getIdDoctor())
                .stream()
                .map(this::toDoctorAppointmentViewDto)
                .toList();
    }

    /**
     * Finds one appointment assigned to the authenticated doctor.
     *
     * @param username the authenticated username
     * @param idAppointment the appointment identifier
     * @return doctor appointment view data
     */
    @Transactional(readOnly = true)
    public DoctorAppointmentViewDto findAppointmentForDoctorView(String username, Integer idAppointment) {
        Doctor doctor = findDoctorByUsername(username);

        Appointment appointment = appointmentRepository.findByIdForDoctor(idAppointment, doctor.getIdDoctor())
                .orElseThrow(() -> new AccessDeniedException("Appointment is not assigned to this doctor."));

        return toDoctorAppointmentViewDto(appointment);
    }

    private Doctor findDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Doctor profile was not found."));
    }

    private DoctorAppointmentViewDto toDoctorAppointmentViewDto(Appointment appointment) {
        return new DoctorAppointmentViewDto(
                appointment.getIdAppointment(),
                appointment.getPatient().getUserAccount().getFullName(),
                appointment.getReason(),
                appointment.getCreatedAt(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getReminderGeneratedAt()
        );
    }
}