package hr.algebra.jwdhealthcare.controller.dev;

import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.DoctorRepository;
import hr.algebra.jwdhealthcare.repository.MedicalRecordRepository;
import hr.algebra.jwdhealthcare.repository.PatientRepository;
import hr.algebra.jwdhealthcare.repository.ReportRepository;
import hr.algebra.jwdhealthcare.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseCheckControllerTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private DatabaseCheckController databaseCheckController;

    @Test
    void checkDatabaseReturnsCountsAndChecksColumnMappings() {
        when(userAccountRepository.count()).thenReturn(1L);
        when(doctorRepository.count()).thenReturn(2L);
        when(patientRepository.count()).thenReturn(3L);
        when(appointmentRepository.count()).thenReturn(4L);
        when(medicalRecordRepository.count()).thenReturn(5L);
        when(reportRepository.count()).thenReturn(6L);

        String result = databaseCheckController.checkDatabase();

        assertTrue(result.contains("Database connection and repository check completed."));
        assertTrue(result.contains("UserAccount count: 1"));
        assertTrue(result.contains("Doctor count: 2"));
        assertTrue(result.contains("Patient count: 3"));
        assertTrue(result.contains("Appointment count: 4"));
        assertTrue(result.contains("MedicalRecord count: 5"));
        assertTrue(result.contains("Report count: 6"));
        assertTrue(result.contains("Column mapping check: completed successfully."));

        PageRequest pageRequest = PageRequest.of(0, 1);

        verify(userAccountRepository).findAll(pageRequest);
        verify(doctorRepository).findAll(pageRequest);
        verify(patientRepository).findAll(pageRequest);
        verify(appointmentRepository).findAll(pageRequest);
        verify(medicalRecordRepository).findAll(pageRequest);
        verify(reportRepository).findAll(pageRequest);
    }
}