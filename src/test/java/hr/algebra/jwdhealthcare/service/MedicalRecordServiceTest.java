package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import hr.algebra.jwdhealthcare.domain.Doctor;
import hr.algebra.jwdhealthcare.domain.MedicalRecord;
import hr.algebra.jwdhealthcare.domain.UserAccount;
import hr.algebra.jwdhealthcare.domain.UserRole;
import hr.algebra.jwdhealthcare.dto.form.MedicalRecordFormDto;
import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.DoctorRepository;
import hr.algebra.jwdhealthcare.repository.MedicalRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @Test
    void findFormForDoctorAppointmentReturnsExistingRecord() {
        Doctor doctor = createDoctor();
        Appointment appointment = createAppointment(doctor, AppointmentStatus.SCHEDULED);
        MedicalRecord medicalRecord = createMedicalRecord(appointment);

        when(doctorRepository.findByUsername("doctor")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByIdForDoctor(10, 5)).thenReturn(Optional.of(appointment));
        when(medicalRecordRepository.findByAppointmentId(10)).thenReturn(Optional.of(medicalRecord));

        MedicalRecordFormDto formDto = medicalRecordService.findFormForDoctorAppointment("doctor", 10);

        assertEquals("Diagnosis A", formDto.getDiagnosis());
        assertEquals("Treatment A", formDto.getTreatment());
        assertEquals("Notes A", formDto.getNotes());
    }

    @Test
    void saveForDoctorAppointmentCreatesNewRecordAndCompletesAppointment() {
        Doctor doctor = createDoctor();
        Appointment appointment = createAppointment(doctor, AppointmentStatus.SCHEDULED);
        MedicalRecordFormDto formDto = createFormDto("Diagnosis B", "Treatment B", "Notes B");

        when(doctorRepository.findByUsername("doctor")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByIdForDoctor(10, 5)).thenReturn(Optional.of(appointment));
        when(medicalRecordRepository.findByAppointmentId(10)).thenReturn(Optional.empty());

        medicalRecordService.saveForDoctorAppointment("doctor", 10, formDto);

        ArgumentCaptor<MedicalRecord> recordCaptor = ArgumentCaptor.forClass(MedicalRecord.class);

        verify(medicalRecordRepository).save(recordCaptor.capture());

        MedicalRecord savedRecord = recordCaptor.getValue();

        assertSame(appointment, savedRecord.getAppointment());
        assertEquals("Diagnosis B", savedRecord.getDiagnosis());
        assertEquals("Treatment B", savedRecord.getTreatment());
        assertEquals("Notes B", savedRecord.getNotes());
        assertNotNull(savedRecord.getUpdatedAt());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    void saveForDoctorAppointmentUpdatesExistingRecordAndCompletesAppointment() {
        Doctor doctor = createDoctor();
        Appointment appointment = createAppointment(doctor, AppointmentStatus.SCHEDULED);
        MedicalRecord existingRecord = createMedicalRecord(appointment);
        MedicalRecordFormDto formDto = createFormDto("Updated diagnosis", "Updated treatment", "Updated notes");

        when(doctorRepository.findByUsername("doctor")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByIdForDoctor(10, 5)).thenReturn(Optional.of(appointment));
        when(medicalRecordRepository.findByAppointmentId(10)).thenReturn(Optional.of(existingRecord));

        medicalRecordService.saveForDoctorAppointment("doctor", 10, formDto);

        verify(medicalRecordRepository).save(existingRecord);

        assertEquals("Updated diagnosis", existingRecord.getDiagnosis());
        assertEquals("Updated treatment", existingRecord.getTreatment());
        assertEquals("Updated notes", existingRecord.getNotes());
        assertNotNull(existingRecord.getUpdatedAt());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    void saveForDoctorAppointmentRejectsAppointmentNotAssignedToDoctor() {
        Doctor doctor = createDoctor();
        MedicalRecordFormDto formDto = createFormDto("Diagnosis", "Treatment", "Notes");

        when(doctorRepository.findByUsername("doctor")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByIdForDoctor(10, 5)).thenReturn(Optional.empty());

        assertThrows(
                AccessDeniedException.class,
                () -> medicalRecordService.saveForDoctorAppointment("doctor", 10, formDto)
        );
    }

    private Doctor createDoctor() {
        UserAccount userAccount = new UserAccount();
        userAccount.setIdUserAccount(2);
        userAccount.setFullName("Test Doctor");
        userAccount.setEmail("doctor@example.com");
        userAccount.setUsername("doctor");
        userAccount.setPasswordHash("password-hash");
        userAccount.setRole(UserRole.DOCTOR);
        userAccount.setEnabled(true);

        Doctor doctor = new Doctor();
        doctor.setIdDoctor(5);
        doctor.setUserAccount(userAccount);
        doctor.setSpecialty("Cardiology");

        return doctor;
    }

    private Appointment createAppointment(Doctor doctor, AppointmentStatus status) {
        Appointment appointment = new Appointment();
        appointment.setIdAppointment(10);
        appointment.setDoctor(doctor);
        appointment.setReason("Test reason");
        appointment.setCreatedAt(LocalDateTime.now().minusDays(1));
        appointment.setScheduledAt(LocalDateTime.now().plusDays(1));
        appointment.setStatus(status);

        return appointment;
    }

    private MedicalRecord createMedicalRecord(Appointment appointment) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setIdMedicalRecord(20);
        medicalRecord.setAppointment(appointment);
        medicalRecord.setDiagnosis("Diagnosis A");
        medicalRecord.setTreatment("Treatment A");
        medicalRecord.setNotes("Notes A");
        medicalRecord.setUpdatedAt(LocalDateTime.now().minusHours(1));

        return medicalRecord;
    }

    private MedicalRecordFormDto createFormDto(String diagnosis, String treatment, String notes) {
        MedicalRecordFormDto formDto = new MedicalRecordFormDto();
        formDto.setDiagnosis(diagnosis);
        formDto.setTreatment(treatment);
        formDto.setNotes(notes);

        return formDto;
    }
}