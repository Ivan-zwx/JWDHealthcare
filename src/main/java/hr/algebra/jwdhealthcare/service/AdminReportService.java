package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.dto.report.AppointmentStatusReportRowDto;
import hr.algebra.jwdhealthcare.dto.report.DailyAppointmentReportRowDto;
import hr.algebra.jwdhealthcare.dto.report.DoctorAppointmentReportRowDto;
import hr.algebra.jwdhealthcare.dto.view.AdminReportViewDto;
import hr.algebra.jwdhealthcare.repository.jdbc.ReportJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Administrator report operations are coordinated between MVC controllers and JdbcTemplate repositories.
 */
@Service
@RequiredArgsConstructor
public class AdminReportService {

    private static final int UPCOMING_REPORT_DAYS = 7;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ReportJdbcRepository reportJdbcRepository;

    /**
     * Finds generated reports prepared for administrator display.
     *
     * @return generated report view data
     */
    @Transactional(readOnly = true)
    public List<AdminReportViewDto> findAllReports() {
        return reportJdbcRepository.findAllReports();
    }

    /**
     * Generates and stores an appointment summary report.
     */
    @Transactional
    public void generateAppointmentSummaryReport() {
        LocalDateTime generatedAt = LocalDateTime.now().withNano(0);
        LocalDateTime upcomingFrom = generatedAt;
        LocalDateTime upcomingTo = generatedAt.plusDays(UPCOMING_REPORT_DAYS);

        List<AppointmentStatusReportRowDto> statusRows = reportJdbcRepository.countAppointmentsByStatus();
        List<DoctorAppointmentReportRowDto> doctorRows = reportJdbcRepository.countAppointmentsByDoctor();
        List<DailyAppointmentReportRowDto> dailyRows = reportJdbcRepository.countAppointmentsByDay(
                upcomingFrom,
                upcomingTo
        );

        String title = "Appointment Summary Report";
        String summary = buildAppointmentSummaryReport(
                generatedAt,
                statusRows,
                doctorRows,
                dailyRows
        );

        reportJdbcRepository.insertReport(title, summary, generatedAt);
    }

    private String buildAppointmentSummaryReport(
            LocalDateTime generatedAt,
            List<AppointmentStatusReportRowDto> statusRows,
            List<DoctorAppointmentReportRowDto> doctorRows,
            List<DailyAppointmentReportRowDto> dailyRows
    ) {
        StringBuilder builder = new StringBuilder();

        builder.append("Healthcare Appointment Summary").append(System.lineSeparator());
        builder.append("Generated at: ")
                .append(generatedAt.format(DATE_TIME_FORMATTER))
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        appendOverallCounts(builder);
        appendStatusRows(builder, statusRows);
        appendDoctorRows(builder, doctorRows);
        appendDailyRows(builder, dailyRows);

        return builder.toString();
    }

    private void appendOverallCounts(StringBuilder builder) {
        builder.append("Overall counts").append(System.lineSeparator());
        builder.append("------------------------------").append(System.lineSeparator());
        builder.append(String.format("%-24s %8d%n", "Appointments", reportJdbcRepository.countAppointments()));
        builder.append(String.format("%-24s %8d%n", "Doctors", reportJdbcRepository.countDoctors()));
        builder.append(String.format("%-24s %8d%n", "Patients", reportJdbcRepository.countPatients()));
        builder.append(String.format("%-24s %8d%n", "Medical records", reportJdbcRepository.countMedicalRecords()));
        builder.append(System.lineSeparator());
    }

    private void appendStatusRows(
            StringBuilder builder,
            List<AppointmentStatusReportRowDto> statusRows
    ) {
        builder.append("Appointments by status").append(System.lineSeparator());
        builder.append("------------------------------").append(System.lineSeparator());
        builder.append(String.format("%-16s %8s%n", "Status", "Count"));

        if (statusRows.isEmpty()) {
            builder.append("No appointment status data was found.").append(System.lineSeparator());
        } else {
            for (AppointmentStatusReportRowDto row : statusRows) {
                builder.append(String.format(
                        "%-16s %8d%n",
                        row.getStatus(),
                        row.getAppointmentCount()
                ));
            }
        }

        builder.append(System.lineSeparator());
    }

    private void appendDoctorRows(
            StringBuilder builder,
            List<DoctorAppointmentReportRowDto> doctorRows
    ) {
        builder.append("Appointments by doctor").append(System.lineSeparator());
        builder.append("------------------------------").append(System.lineSeparator());
        builder.append(String.format(
                "%-28s %-22s %9s %9s %9s %7s%n",
                "Doctor",
                "Specialty",
                "Scheduled",
                "Completed",
                "Cancelled",
                "Total"
        ));

        if (doctorRows.isEmpty()) {
            builder.append("No doctor appointment data was found.").append(System.lineSeparator());
        } else {
            for (DoctorAppointmentReportRowDto row : doctorRows) {
                builder.append(String.format(
                        "%-28s %-22s %9d %9d %9d %7d%n",
                        limit(row.getDoctorName(), 28),
                        limit(row.getSpecialty(), 22),
                        row.getScheduledCount(),
                        row.getCompletedCount(),
                        row.getCancelledCount(),
                        row.getTotalCount()
                ));
            }
        }

        builder.append(System.lineSeparator());
    }

    private void appendDailyRows(
            StringBuilder builder,
            List<DailyAppointmentReportRowDto> dailyRows
    ) {
        builder.append("Appointments by day for the next ")
                .append(UPCOMING_REPORT_DAYS)
                .append(" days")
                .append(System.lineSeparator());
        builder.append("------------------------------").append(System.lineSeparator());
        builder.append(String.format("%-16s %8s%n", "Date", "Count"));

        if (dailyRows.isEmpty()) {
            builder.append("No upcoming appointment data was found.").append(System.lineSeparator());
        } else {
            for (DailyAppointmentReportRowDto row : dailyRows) {
                builder.append(String.format(
                        "%-16s %8d%n",
                        row.getAppointmentDate(),
                        row.getAppointmentCount()
                ));
            }
        }

        builder.append(System.lineSeparator());
    }

    private String limit(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength - 3) + "...";
    }
}