package hr.algebra.jwdhealthcare.scheduler;

import hr.algebra.jwdhealthcare.service.AdminReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportSchedulerTest {

    @Mock
    private AdminReportService adminReportService;

    @InjectMocks
    private ReportScheduler reportScheduler;

    @Test
    void generateScheduledAppointmentSummaryReportCallsReportService() {
        when(adminReportService.generateScheduledAppointmentSummaryReportIfNeeded())
                .thenReturn(true);

        reportScheduler.generateScheduledAppointmentSummaryReport();

        verify(adminReportService).generateScheduledAppointmentSummaryReportIfNeeded();
    }

    @Test
    void generateScheduledAppointmentSummaryReportHandlesServiceException() {
        doThrow(new RuntimeException("Report failure"))
                .when(adminReportService)
                .generateScheduledAppointmentSummaryReportIfNeeded();

        assertDoesNotThrow(() -> reportScheduler.generateScheduledAppointmentSummaryReport());

        verify(adminReportService).generateScheduledAppointmentSummaryReportIfNeeded();
    }
}