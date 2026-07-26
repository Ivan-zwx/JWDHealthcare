package hr.algebra.jwdhealthcare.scheduler;

import hr.algebra.jwdhealthcare.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Appointment reminder generation is run by the application scheduler.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentReminderScheduler {

    private final ReminderService reminderService;

    /**
     * Generates appointment reminders on a fixed application-managed schedule.
     */
    @Scheduled(
            initialDelayString = "${app.reminders.initial-delay-ms}",
            fixedDelayString = "${app.reminders.fixed-delay-ms}"
    )
    public void generateAppointmentReminders() {
        try {
            int generatedCount = reminderService.generateAppointmentReminders();

            if (generatedCount > 0) {
                log.info("Generated {} appointment reminders.", generatedCount);
            }
        } catch (RuntimeException exception) {
            log.error("Appointment reminder generation failed.", exception);
        }
    }
}