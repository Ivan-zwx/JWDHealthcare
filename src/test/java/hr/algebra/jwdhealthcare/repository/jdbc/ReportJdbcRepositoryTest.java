package hr.algebra.jwdhealthcare.repository.jdbc;

import hr.algebra.jwdhealthcare.dto.report.AppointmentStatusReportRowDto;
import hr.algebra.jwdhealthcare.dto.report.DailyAppointmentReportRowDto;
import hr.algebra.jwdhealthcare.dto.report.DoctorAppointmentReportRowDto;
import hr.algebra.jwdhealthcare.dto.view.AdminReportViewDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportJdbcRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ReportJdbcRepository reportJdbcRepository;

    @Test
    void countAppointmentsReturnsJdbcTemplateCount() {
        when(jdbcTemplate.queryForObject("select count(*) from [Appointment]", Integer.class))
                .thenReturn(5);

        int count = reportJdbcRepository.countAppointments();

        assertEquals(5, count);
    }

    @Test
    void countAppointmentsReturnsZeroWhenJdbcTemplateReturnsNull() {
        when(jdbcTemplate.queryForObject("select count(*) from [Appointment]", Integer.class))
                .thenReturn(null);

        int count = reportJdbcRepository.countAppointments();

        assertEquals(0, count);
    }

    @Test
    void countPatientsReturnsJdbcTemplateCount() {
        when(jdbcTemplate.queryForObject("select count(*) from [Patient]", Integer.class))
                .thenReturn(3);

        int count = reportJdbcRepository.countPatients();

        assertEquals(3, count);
    }

    @Test
    void countDoctorsReturnsJdbcTemplateCount() {
        when(jdbcTemplate.queryForObject("select count(*) from [Doctor]", Integer.class))
                .thenReturn(2);

        int count = reportJdbcRepository.countDoctors();

        assertEquals(2, count);
    }

    @Test
    void countMedicalRecordsReturnsJdbcTemplateCount() {
        when(jdbcTemplate.queryForObject("select count(*) from [MedicalRecord]", Integer.class))
                .thenReturn(4);

        int count = reportJdbcRepository.countMedicalRecords();

        assertEquals(4, count);
    }

    @Test
    void countAppointmentsByStatusMapsRows() throws Exception {
        when(jdbcTemplate.query(
                anyString(),
                ArgumentMatchers.<RowMapper<AppointmentStatusReportRowDto>>any()
        )).thenAnswer(invocation -> {
            RowMapper<AppointmentStatusReportRowDto> rowMapper = invocation.getArgument(1);
            ResultSet resultSet = mock(ResultSet.class);

            when(resultSet.getString("Status")).thenReturn("SCHEDULED");
            when(resultSet.getInt("AppointmentCount")).thenReturn(7);

            return List.of(rowMapper.mapRow(resultSet, 0));
        });

        List<AppointmentStatusReportRowDto> rows = reportJdbcRepository.countAppointmentsByStatus();

        assertEquals(1, rows.size());
        assertEquals("SCHEDULED", rows.getFirst().getStatus());
        assertEquals(7, rows.getFirst().getAppointmentCount());
    }

    @Test
    void countAppointmentsByDoctorMapsRows() throws Exception {
        when(jdbcTemplate.query(
                anyString(),
                ArgumentMatchers.<RowMapper<DoctorAppointmentReportRowDto>>any()
        )).thenAnswer(invocation -> {
            RowMapper<DoctorAppointmentReportRowDto> rowMapper = invocation.getArgument(1);
            ResultSet resultSet = mock(ResultSet.class);

            when(resultSet.getString("DoctorName")).thenReturn("Dr. Test Doctor");
            when(resultSet.getString("Specialty")).thenReturn("Cardiology");
            when(resultSet.getInt("ScheduledCount")).thenReturn(3);
            when(resultSet.getInt("CompletedCount")).thenReturn(2);
            when(resultSet.getInt("CancelledCount")).thenReturn(1);
            when(resultSet.getInt("TotalCount")).thenReturn(6);

            return List.of(rowMapper.mapRow(resultSet, 0));
        });

        List<DoctorAppointmentReportRowDto> rows = reportJdbcRepository.countAppointmentsByDoctor();

        assertEquals(1, rows.size());
        assertEquals("Dr. Test Doctor", rows.getFirst().getDoctorName());
        assertEquals("Cardiology", rows.getFirst().getSpecialty());
        assertEquals(3, rows.getFirst().getScheduledCount());
        assertEquals(2, rows.getFirst().getCompletedCount());
        assertEquals(1, rows.getFirst().getCancelledCount());
        assertEquals(6, rows.getFirst().getTotalCount());
    }

    @Test
    void countAppointmentsByDayMapsRows() throws Exception {
        LocalDate appointmentDate = LocalDate.of(2026, 8, 29);
        LocalDateTime fromDateTime = LocalDateTime.of(2026, 8, 29, 0, 0);
        LocalDateTime toDateTime = LocalDateTime.of(2026, 8, 30, 0, 0);

        when(jdbcTemplate.query(
                anyString(),
                ArgumentMatchers.<RowMapper<DailyAppointmentReportRowDto>>any(),
                eq(fromDateTime),
                eq(toDateTime)
        )).thenAnswer(invocation -> {
            RowMapper<DailyAppointmentReportRowDto> rowMapper = invocation.getArgument(1);
            ResultSet resultSet = mock(ResultSet.class);

            when(resultSet.getDate("AppointmentDate")).thenReturn(Date.valueOf(appointmentDate));
            when(resultSet.getInt("AppointmentCount")).thenReturn(5);

            return List.of(rowMapper.mapRow(resultSet, 0));
        });

        List<DailyAppointmentReportRowDto> rows = reportJdbcRepository.countAppointmentsByDay(
                fromDateTime,
                toDateTime
        );

        assertEquals(1, rows.size());
        assertEquals(appointmentDate, rows.getFirst().getAppointmentDate());
        assertEquals(5, rows.getFirst().getAppointmentCount());
    }

    @Test
    void reportExistsByTitleAndGeneratedAtBetweenReturnsTrueWhenCountIsPositive() {
        LocalDateTime fromDateTime = LocalDateTime.of(2026, 8, 29, 0, 0);
        LocalDateTime toDateTime = LocalDateTime.of(2026, 8, 30, 0, 0);

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq("Scheduled Appointment Summary Report"),
                eq(fromDateTime),
                eq(toDateTime)
        )).thenReturn(1);

        boolean exists = reportJdbcRepository.reportExistsByTitleAndGeneratedAtBetween(
                "Scheduled Appointment Summary Report",
                fromDateTime,
                toDateTime
        );

        assertTrue(exists);
    }

    @Test
    void reportExistsByTitleAndGeneratedAtBetweenReturnsFalseWhenCountIsZero() {
        LocalDateTime fromDateTime = LocalDateTime.of(2026, 8, 29, 0, 0);
        LocalDateTime toDateTime = LocalDateTime.of(2026, 8, 30, 0, 0);

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq("Scheduled Appointment Summary Report"),
                eq(fromDateTime),
                eq(toDateTime)
        )).thenReturn(0);

        boolean exists = reportJdbcRepository.reportExistsByTitleAndGeneratedAtBetween(
                "Scheduled Appointment Summary Report",
                fromDateTime,
                toDateTime
        );

        assertFalse(exists);
    }

    @Test
    void insertReportCallsJdbcTemplateUpdate() {
        LocalDateTime generatedAt = LocalDateTime.of(2026, 8, 29, 12, 0);

        reportJdbcRepository.insertReport("Title", "Summary", generatedAt);

        verify(jdbcTemplate).update(
                anyString(),
                eq("Title"),
                eq("Summary"),
                eq(generatedAt)
        );
    }

    @Test
    void findAllReportsMapsRows() throws Exception {
        LocalDateTime generatedAt = LocalDateTime.of(2026, 8, 29, 12, 0);

        when(jdbcTemplate.query(
                anyString(),
                ArgumentMatchers.<RowMapper<AdminReportViewDto>>any()
        )).thenAnswer(invocation -> {
            RowMapper<AdminReportViewDto> rowMapper = invocation.getArgument(1);
            ResultSet resultSet = mock(ResultSet.class);

            when(resultSet.getInt("IDReport")).thenReturn(1);
            when(resultSet.getString("Title")).thenReturn("Appointment Summary Report");
            when(resultSet.getString("Summary")).thenReturn("Report summary");
            when(resultSet.getTimestamp("GeneratedAt")).thenReturn(Timestamp.valueOf(generatedAt));

            return List.of(rowMapper.mapRow(resultSet, 0));
        });

        List<AdminReportViewDto> reports = reportJdbcRepository.findAllReports();

        assertEquals(1, reports.size());
        assertEquals(1, reports.getFirst().getIdReport());
        assertEquals("Appointment Summary Report", reports.getFirst().getTitle());
        assertEquals("Report summary", reports.getFirst().getSummary());
        assertEquals(generatedAt, reports.getFirst().getGeneratedAt());
    }
}