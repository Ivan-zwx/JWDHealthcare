package hr.algebra.jwdhealthcare.scheduler;

import hr.algebra.jwdhealthcare.service.ReminderService;
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
class AppointmentReminderSchedulerTest {

    @Mock
    private ReminderService reminderService;

    @InjectMocks
    private AppointmentReminderScheduler appointmentReminderScheduler;

    @Test
    void generateAppointmentRemindersCallsReminderService() {
        when(reminderService.generateAppointmentReminders()).thenReturn(1);

        appointmentReminderScheduler.generateAppointmentReminders();

        verify(reminderService).generateAppointmentReminders();
    }

    @Test
    void generateAppointmentRemindersHandlesServiceException() {
        doThrow(new RuntimeException("Reminder failure"))
                .when(reminderService)
                .generateAppointmentReminders();

        assertDoesNotThrow(() -> appointmentReminderScheduler.generateAppointmentReminders());

        verify(reminderService).generateAppointmentReminders();
    }
}