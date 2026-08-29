package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import hr.algebra.jwdhealthcare.domain.Doctor;
import hr.algebra.jwdhealthcare.domain.Patient;
import hr.algebra.jwdhealthcare.domain.UserAccount;
import hr.algebra.jwdhealthcare.domain.UserRole;
import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.DoctorRepository;
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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void findScheduleForDoctorUsesAuthenticatedDoctor() {
        Doctor doctor = createDoctor();

        when(doctorRepository.findByUsername("doctor")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findAllForDoctorSchedule(5)).thenReturn(List.of(createAppointment(doctor)));

        List<?> schedule = doctorService.findScheduleForDoctor("doctor");

        assertEquals(1, schedule.size());
        verify(doctorRepository).findByUsername("doctor");
        verify(appointmentRepository).findAllForDoctorSchedule(5);
    }

    private Appointment createAppointment(Doctor doctor) {
        Appointment appointment = new Appointment();
        appointment.setIdAppointment(10);
        appointment.setDoctor(doctor);
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