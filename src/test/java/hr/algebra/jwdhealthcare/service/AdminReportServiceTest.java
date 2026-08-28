package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.dto.report.AppointmentStatusReportRowDto;
import hr.algebra.jwdhealthcare.dto.report.DailyAppointmentReportRowDto;
import hr.algebra.jwdhealthcare.dto.report.DoctorAppointmentReportRowDto;
import hr.algebra.jwdhealthcare.dto.view.AdminReportViewDto;
import hr.algebra.jwdhealthcare.repository.jdbc.ReportJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminReportServiceTest {

    @Mock
    private ReportJdbcRepository reportJdbcRepository;

    @InjectMocks
    private AdminReportService adminReportService;

    @Test
    void findAllReportsReturnsRepositoryReports() {
        List<AdminReportViewDto> reports = List.of(new AdminReportViewDto(
                1,
                "Appointment Summary Report",
                "Summary",
                LocalDateTime.now()
        ));

        when(reportJdbcRepository.findAllReports()).thenReturn(reports);

        List<AdminReportViewDto> result = adminReportService.findAllReports();

        assertSame(reports, result);
    }

    @Test
    void generateAppointmentSummaryReportInsertsManualReport() {
        prepareReportData();

        adminReportService.generateAppointmentSummaryReport();

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> summaryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> generatedAtCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(reportJdbcRepository).insertReport(
                titleCaptor.capture(),
                summaryCaptor.capture(),
                generatedAtCaptor.capture()
        );

        assertEquals("Appointment Summary Report", titleCaptor.getValue());
        assertTrue(summaryCaptor.getValue().contains("Appointment Summary Report"));
        assertTrue(summaryCaptor.getValue().contains("Report source: Manual"));
        assertTrue(summaryCaptor.getValue().contains("Overall counts"));
        assertTrue(summaryCaptor.getValue().contains("Appointments by status"));
        assertTrue(summaryCaptor.getValue().contains("Appointments by doctor"));
        assertTrue(summaryCaptor.getValue().contains("Appointments by day for the next 7 days"));
        assertTrue(summaryCaptor.getValue().contains("SCHEDULED"));
        assertTrue(summaryCaptor.getValue().contains("Dr. Test Doctor"));
        assertTrue(summaryCaptor.getValue().contains("Cardiology"));
        assertTrue(summaryCaptor.getValue().contains(LocalDate.now().plusDays(1).toString()));
        assertFalse(generatedAtCaptor.getValue().isAfter(LocalDateTime.now()));
    }

    @Test
    void generateScheduledAppointmentSummaryReportIfNeededSkipsExistingDailyReport() {
        when(reportJdbcRepository.reportExistsByTitleAndGeneratedAtBetween(
                eq("Scheduled Appointment Summary Report"),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(true);

        boolean generated = adminReportService.generateScheduledAppointmentSummaryReportIfNeeded();

        assertFalse(generated);
        verify(reportJdbcRepository, never()).insertReport(
                anyString(),
                anyString(),
                any(LocalDateTime.class)
        );
    }

    @Test
    void generateScheduledAppointmentSummaryReportIfNeededInsertsScheduledReportWhenMissing() {
        when(reportJdbcRepository.reportExistsByTitleAndGeneratedAtBetween(
                eq("Scheduled Appointment Summary Report"),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(false);
        prepareReportData();

        boolean generated = adminReportService.generateScheduledAppointmentSummaryReportIfNeeded();

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> summaryCaptor = ArgumentCaptor.forClass(String.class);

        verify(reportJdbcRepository).insertReport(
                titleCaptor.capture(),
                summaryCaptor.capture(),
                any(LocalDateTime.class)
        );

        assertTrue(generated);
        assertEquals("Scheduled Appointment Summary Report", titleCaptor.getValue());
        assertTrue(summaryCaptor.getValue().contains("Report source: Scheduled"));
    }

    private void prepareReportData() {
        when(reportJdbcRepository.countAppointments()).thenReturn(5);
        when(reportJdbcRepository.countDoctors()).thenReturn(2);
        when(reportJdbcRepository.countPatients()).thenReturn(3);
        when(reportJdbcRepository.countMedicalRecords()).thenReturn(4);

        when(reportJdbcRepository.countAppointmentsByStatus()).thenReturn(List.of(
                new AppointmentStatusReportRowDto("SCHEDULED", 3),
                new AppointmentStatusReportRowDto("COMPLETED", 2)
        ));

        when(reportJdbcRepository.countAppointmentsByDoctor()).thenReturn(List.of(
                new DoctorAppointmentReportRowDto(
                        "Dr. Test Doctor",
                        "Cardiology",
                        2,
                        1,
                        0,
                        3
                )
        ));

        when(reportJdbcRepository.countAppointmentsByDay(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(
                new DailyAppointmentReportRowDto(LocalDate.now().plusDays(1), 2)
        ));
    }
}