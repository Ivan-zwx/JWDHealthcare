package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Patient reminder pages are handled through MVC endpoints.
 */
@Controller
@RequiredArgsConstructor
public class PatientReminderController {

    private final ReminderService reminderService;

    /**
     * Displays generated appointment reminders for the authenticated patient.
     *
     * @param principal the authenticated user principal
     * @param model the model used by the reminder list view
     * @return the patient reminder list view
     */
    @GetMapping("/patient/reminders")
    public String reminders(Principal principal, Model model) {
        model.addAttribute("reminders", reminderService.findRemindersForPatient(principal.getName()));
        return "patient/reminders/list";
    }
}