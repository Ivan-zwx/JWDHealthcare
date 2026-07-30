package hr.algebra.jwdhealthcare.scheduler;

import hr.algebra.jwdhealthcare.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled report generation is run by the application scheduler.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {

    private final AdminReportService adminReportService;

    /**
     * Generates a scheduled appointment summary report when one has not yet been generated for the day.
     */
    @Scheduled(
            initialDelayString = "${app.reports.scheduler.initial-delay-ms}",
            fixedDelayString = "${app.reports.scheduler.fixed-delay-ms}"
    )
    public void generateScheduledAppointmentSummaryReport() {
        try {
            boolean generated = adminReportService.generateScheduledAppointmentSummaryReportIfNeeded();

            if (generated) {
                log.info("Generated scheduled appointment summary report.");
            }
        } catch (RuntimeException exception) {
            log.error("Scheduled appointment summary report generation failed.", exception);
        }
    }
}