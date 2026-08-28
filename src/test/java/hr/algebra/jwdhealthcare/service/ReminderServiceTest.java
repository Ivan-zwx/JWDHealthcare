package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private ReminderService reminderService;

    @Test
    void generateAppointmentRemindersMarksReturnedAppointments() {
        ReflectionTestUtils.setField(reminderService, "reminderLookAheadHours", 24);

        Appointment appointment = new Appointment();
        appointment.setIdAppointment(1);
        appointment.setScheduledAt(LocalDateTime.now().plusHours(3));
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findUpcomingAppointmentsNeedingReminder(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(AppointmentStatus.SCHEDULED)
        )).thenReturn(List.of(appointment));

        int generatedCount = reminderService.generateAppointmentReminders();

        assertNotNull(appointment.getReminderGeneratedAt());
        verify(appointmentRepository).saveAll(List.of(appointment));
        org.junit.jupiter.api.Assertions.assertEquals(1, generatedCount);
    }
}