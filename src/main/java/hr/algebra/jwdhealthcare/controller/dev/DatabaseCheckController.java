package hr.algebra.jwdhealthcare.controller.dev;

import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.DoctorRepository;
import hr.algebra.jwdhealthcare.repository.MedicalRecordRepository;
import hr.algebra.jwdhealthcare.repository.PatientRepository;
import hr.algebra.jwdhealthcare.repository.ReportRepository;
import hr.algebra.jwdhealthcare.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Temporary database checks are exposed during development.
 */
@RestController
@RequiredArgsConstructor
public class DatabaseCheckController {

    private final UserAccountRepository userAccountRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final ReportRepository reportRepository;

    /**
     * Returns row counts and performs a basic column-mapping check for all mapped database entities.
     *
     * @return a plain-text database connectivity and mapping summary
     */
    @GetMapping("/dev/db-check")
    public String checkDatabase() {
        long userAccountCount = userAccountRepository.count();
        long doctorCount = doctorRepository.count();
        long patientCount = patientRepository.count();
        long appointmentCount = appointmentRepository.count();
        long medicalRecordCount = medicalRecordRepository.count();
        long reportCount = reportRepository.count();

        checkColumnMappings();

        return """
                Database connection and repository check completed.

                Row count check:
                UserAccount count: %d
                Doctor count: %d
                Patient count: %d
                Appointment count: %d
                MedicalRecord count: %d
                Report count: %d

                Column mapping check: completed successfully.
                """.formatted(
                userAccountCount,
                doctorCount,
                patientCount,
                appointmentCount,
                medicalRecordCount,
                reportCount
        );
    }

    /**
     * Executes one small entity select for each repository.
     * This confirms that mapped table columns can be resolved by the database.
     */
    private void checkColumnMappings() {
        checkRepositoryColumns(userAccountRepository);
        checkRepositoryColumns(doctorRepository);
        checkRepositoryColumns(patientRepository);
        checkRepositoryColumns(appointmentRepository);
        checkRepositoryColumns(medicalRecordRepository);
        checkRepositoryColumns(reportRepository);
    }

    /**
     * Executes a one-row page query for a repository.
     * The query is intentionally small, but it still forces mapped columns to be selected.
     *
     * @param repository the repository whose mapped columns should be checked
     */
    private void checkRepositoryColumns(JpaRepository<?, Integer> repository) {
        repository.findAll(PageRequest.of(0, 1));
    }
}