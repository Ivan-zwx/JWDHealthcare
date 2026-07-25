package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import hr.algebra.jwdhealthcare.domain.Doctor;
import hr.algebra.jwdhealthcare.domain.MedicalRecord;
import hr.algebra.jwdhealthcare.dto.form.MedicalRecordFormDto;
import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.DoctorRepository;
import hr.algebra.jwdhealthcare.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Medical record business operations are coordinated between MVC controllers and repositories.
 */
@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    /**
     * Finds existing medical record data for a doctor's appointment, or prepares an empty form.
     *
     * @param username the authenticated username
     * @param idAppointment the appointment identifier
     * @return medical record form data
     */
    @Transactional(readOnly = true)
    public MedicalRecordFormDto findFormForDoctorAppointment(String username, Integer idAppointment) {
        Appointment appointment = findAppointmentForDoctor(username, idAppointment);
        validateRecordCanBeEdited(appointment);

        return medicalRecordRepository.findByAppointmentId(idAppointment)
                .map(this::toFormDto)
                .orElseGet(MedicalRecordFormDto::new);
    }

    /**
     * Saves medical record data for a doctor's appointment.
     *
     * @param username the authenticated username
     * @param idAppointment the appointment identifier
     * @param medicalRecordFormDto the submitted medical record form data
     */
    @Transactional
    public void saveForDoctorAppointment(
            String username,
            Integer idAppointment,
            MedicalRecordFormDto medicalRecordFormDto
    ) {
        Appointment appointment = findAppointmentForDoctor(username, idAppointment);
        validateRecordCanBeEdited(appointment);

        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(idAppointment)
                .orElseGet(() -> createMedicalRecord(appointment));

        medicalRecord.setDiagnosis(normalizeText(medicalRecordFormDto.getDiagnosis()));
        medicalRecord.setTreatment(normalizeText(medicalRecordFormDto.getTreatment()));
        medicalRecord.setNotes(normalizeText(medicalRecordFormDto.getNotes()));
        medicalRecord.setUpdatedAt(LocalDateTime.now().withNano(0));

        appointment.setStatus(AppointmentStatus.COMPLETED);

        medicalRecordRepository.save(medicalRecord);
        appointmentRepository.save(appointment);
    }

    private Appointment findAppointmentForDoctor(String username, Integer idAppointment) {
        Doctor doctor = doctorRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Doctor profile was not found."));

        return appointmentRepository.findByIdForDoctor(idAppointment, doctor.getIdDoctor())
                .orElseThrow(() -> new AccessDeniedException("Appointment is not assigned to this doctor."));
    }

    private void validateRecordCanBeEdited(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AccessDeniedException("Medical records cannot be edited for cancelled appointments.");
        }
    }

    private MedicalRecord createMedicalRecord(Appointment appointment) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);
        return medicalRecord;
    }

    private MedicalRecordFormDto toFormDto(MedicalRecord medicalRecord) {
        MedicalRecordFormDto medicalRecordFormDto = new MedicalRecordFormDto();
        medicalRecordFormDto.setIdMedicalRecord(medicalRecord.getIdMedicalRecord());
        medicalRecordFormDto.setDiagnosis(medicalRecord.getDiagnosis());
        medicalRecordFormDto.setTreatment(medicalRecord.getTreatment());
        medicalRecordFormDto.setNotes(medicalRecord.getNotes());

        return medicalRecordFormDto;
    }

    private String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }
}