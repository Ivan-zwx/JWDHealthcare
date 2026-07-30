package hr.algebra.jwdhealthcare.repository.jdbc;

import hr.algebra.jwdhealthcare.dto.report.AppointmentStatusReportRowDto;
import hr.algebra.jwdhealthcare.dto.report.DailyAppointmentReportRowDto;
import hr.algebra.jwdhealthcare.dto.report.DoctorAppointmentReportRowDto;
import hr.algebra.jwdhealthcare.dto.view.AdminReportViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JdbcTemplate data access operations are provided for generated reports.
 */
@Repository
@RequiredArgsConstructor
public class ReportJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Counts appointments grouped by status.
     *
     * @return appointment status report rows
     */
    public List<AppointmentStatusReportRowDto> countAppointmentsByStatus() {
        String sql = """
                select
                    [Status],
                    count(*) as AppointmentCount
                from [Appointment]
                group by [Status]
                order by [Status]
                """;

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> new AppointmentStatusReportRowDto(
                        resultSet.getString("Status"),
                        resultSet.getInt("AppointmentCount")
                )
        );
    }

    /**
     * Counts appointments grouped by doctor.
     *
     * @return doctor appointment report rows
     */
    public List<DoctorAppointmentReportRowDto> countAppointmentsByDoctor() {
        String sql = """
                select
                    ua.[FullName] as DoctorName,
                    d.[Specialty] as Specialty,
                    sum(case when a.[Status] = 'SCHEDULED' then 1 else 0 end) as ScheduledCount,
                    sum(case when a.[Status] = 'COMPLETED' then 1 else 0 end) as CompletedCount,
                    sum(case when a.[Status] = 'CANCELLED' then 1 else 0 end) as CancelledCount,
                    count(a.[IDAppointment]) as TotalCount
                from [Doctor] d
                join [UserAccount] ua
                    on ua.[IDUserAccount] = d.[UserAccountID]
                left join [Appointment] a
                    on a.[DoctorID] = d.[IDDoctor]
                group by
                    ua.[FullName],
                    d.[Specialty]
                order by
                    ua.[FullName]
                """;

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> new DoctorAppointmentReportRowDto(
                        resultSet.getString("DoctorName"),
                        resultSet.getString("Specialty"),
                        resultSet.getInt("ScheduledCount"),
                        resultSet.getInt("CompletedCount"),
                        resultSet.getInt("CancelledCount"),
                        resultSet.getInt("TotalCount")
                )
        );
    }

    /**
     * Counts appointments grouped by scheduled date within a date range.
     *
     * @param fromDateTime the lower scheduling boundary
     * @param toDateTime the upper scheduling boundary
     * @return daily appointment report rows
     */
    public List<DailyAppointmentReportRowDto> countAppointmentsByDay(
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    ) {
        String sql = """
                select
                    cast([ScheduledAt] as date) as AppointmentDate,
                    count(*) as AppointmentCount
                from [Appointment]
                where [ScheduledAt] >= ?
                  and [ScheduledAt] < ?
                group by cast([ScheduledAt] as date)
                order by AppointmentDate
                """;

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> {
                    Date appointmentDate = resultSet.getDate("AppointmentDate");

                    return new DailyAppointmentReportRowDto(
                            appointmentDate.toLocalDate(),
                            resultSet.getInt("AppointmentCount")
                    );
                },
                fromDateTime,
                toDateTime
        );
    }

    /**
     * Counts all appointments.
     *
     * @return total appointment count
     */
    public int countAppointments() {
        return queryForRequiredInt("select count(*) from [Appointment]");
    }

    /**
     * Counts all patients.
     *
     * @return total patient count
     */
    public int countPatients() {
        return queryForRequiredInt("select count(*) from [Patient]");
    }

    /**
     * Counts all doctors.
     *
     * @return total doctor count
     */
    public int countDoctors() {
        return queryForRequiredInt("select count(*) from [Doctor]");
    }

    /**
     * Counts all medical records.
     *
     * @return total medical record count
     */
    public int countMedicalRecords() {
        return queryForRequiredInt("select count(*) from [MedicalRecord]");
    }

    /**
     * Checks whether a report with the selected title already exists in a generation time range.
     *
     * @param title the report title
     * @param fromDateTime the lower generation boundary
     * @param toDateTime the upper generation boundary
     * @return true if the report already exists
     */
    public boolean reportExistsByTitleAndGeneratedAtBetween(
            String title,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    ) {
        String sql = """
                select count(*)
                from [Report]
                where [Title] = ?
                  and [GeneratedAt] >= ?
                  and [GeneratedAt] < ?
                """;

        Integer value = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                title,
                fromDateTime,
                toDateTime
        );

        return value != null && value > 0;
    }

    /**
     * Inserts a generated report.
     *
     * @param title the report title
     * @param summary the generated report summary
     * @param generatedAt the generation timestamp
     */
    public void insertReport(String title, String summary, LocalDateTime generatedAt) {
        String sql = """
                insert into [Report] ([Title], [Summary], [GeneratedAt])
                values (?, ?, ?)
                """;

        jdbcTemplate.update(sql, title, summary, generatedAt);
    }

    /**
     * Finds all generated reports.
     *
     * @return reports ordered by generation time
     */
    public List<AdminReportViewDto> findAllReports() {
        String sql = """
                select
                    [IDReport],
                    [Title],
                    [Summary],
                    [GeneratedAt]
                from [Report]
                order by [GeneratedAt] desc, [IDReport] desc
                """;

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> {
                    Timestamp generatedAt = resultSet.getTimestamp("GeneratedAt");

                    return new AdminReportViewDto(
                            resultSet.getInt("IDReport"),
                            resultSet.getString("Title"),
                            resultSet.getString("Summary"),
                            generatedAt.toLocalDateTime()
                    );
                }
        );
    }

    private int queryForRequiredInt(String sql) {
        Integer value = jdbcTemplate.queryForObject(sql, Integer.class);

        if (value == null) {
            return 0;
        }

        return value;
    }
}