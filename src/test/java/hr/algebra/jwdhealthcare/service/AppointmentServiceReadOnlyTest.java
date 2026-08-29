package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import hr.algebra.jwdhealthcare.domain.Doctor;
import hr.algebra.jwdhealthcare.domain.Patient;
import hr.algebra.jwdhealthcare.domain.UserAccount;
import hr.algebra.jwdhealthcare.domain.UserRole;
import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.DoctorRepository;
import hr.algebra.jwdhealthcare.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceReadOnlyTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void findAllForAdminViewReturnsMappedAppointments() {
        Appointment appointment = createAppointment();

        when(appointmentRepository.findAllForAdminList()).thenReturn(List.of(appointment));

        List<?> appointments = appointmentService.findAllForAdminView();

        assertEquals(1, appointments.size());
        verify(appointmentRepository).findAllForAdminList();
    }

    @Test
    void findAllForPatientViewUsesAuthenticatedPatient() {
        Patient patient = createPatient();

        when(patientRepository.findByUsername("patient")).thenReturn(Optional.of(patient));
        when(appointmentRepository.findAllForPatientView(7)).thenReturn(List.of(createAppointment()));

        List<?> appointments = appointmentService.findAllForPatientView("patient");

        assertEquals(1, appointments.size());
        verify(patientRepository).findByUsername("patient");
        verify(appointmentRepository).findAllForPatientView(7);
    }

    @Test
    void findDoctorOptionsReturnsMappedDoctors() {
        Doctor doctor = createDoctor();

        when(doctorRepository.findAllWithUserAccount()).thenReturn(List.of(doctor));

        List<?> doctorOptions = appointmentService.findDoctorOptions();

        assertEquals(1, doctorOptions.size());
        verify(doctorRepository).findAllWithUserAccount();
    }

    @Test
    void findPatientOptionsReturnsMappedPatients() {
        Patient patient = createPatient();

        when(patientRepository.findAllWithUserAccount()).thenReturn(List.of(patient));

        List<?> patientOptions = appointmentService.findPatientOptions();

        assertEquals(1, patientOptions.size());
        verify(patientRepository).findAllWithUserAccount();
    }

    @Test
    void findStatusOptionsReturnsAllAppointmentStatuses() {
        List<?> statusOptions = appointmentService.findStatusOptions();

        assertEquals(AppointmentStatus.values().length, statusOptions.size());
    }

    private Appointment createAppointment() {
        Appointment appointment = new Appointment();
        appointment.setIdAppointment(10);
        appointment.setDoctor(createDoctor());
        appointment.setPatient(createPatient());
        appointment.setReason("Test appointment");
        appointment.setCreatedAt(LocalDateTime.of(2026, 8, 29, 9, 0));
        appointment.setScheduledAt(LocalDateTime.of(2026, 8, 30, 10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointment;
    }

    private Doctor createDoctor() {
        Doctor doctor = new Doctor();
        doctor.setIdDoctor(5);
        doctor.setUserAccount(createUserAccount(
                2,
                "Test Doctor",
                "doctor",
                UserRole.DOCTOR
        ));
        doctor.setSpecialty("Cardiology");

        return doctor;
    }

    private Patient createPatient() {
        Patient patient = new Patient();
        patient.setIdPatient(7);
        patient.setUserAccount(createUserAccount(
                3,
                "Test Patient",
                "patient",
                UserRole.PATIENT
        ));
        patient.setAddress("Test address");
        patient.setPhone("123456789");

        return patient;
    }

    private UserAccount createUserAccount(
            Integer idUserAccount,
            String fullName,
            String username,
            UserRole role
    ) {
        UserAccount userAccount = new UserAccount();
        userAccount.setIdUserAccount(idUserAccount);
        userAccount.setFullName(fullName);
        userAccount.setEmail(username + "@example.com");
        userAccount.setUsername(username);
        userAccount.setPasswordHash("password-hash");
        userAccount.setRole(role);
        userAccount.setEnabled(true);

        return userAccount;
    }
}