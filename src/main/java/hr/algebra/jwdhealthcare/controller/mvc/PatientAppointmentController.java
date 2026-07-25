package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.form.PatientAppointmentFormDto;
import hr.algebra.jwdhealthcare.exception.AppointmentSchedulingException;
import hr.algebra.jwdhealthcare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Patient appointment pages are handled through MVC endpoints.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/patient/appointments")
public class PatientAppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Displays appointments for the authenticated patient.
     *
     * @param principal the authenticated user principal
     * @param model the model used by the patient appointment list view
     * @return the patient appointment list view
     */
    @GetMapping
    public String list(Principal principal, Model model) {
        model.addAttribute("appointments", appointmentService.findAllForPatientView(principal.getName()));
        return "patient/appointments/list";
    }

    /**
     * Displays the patient appointment booking form.
     *
     * @param model the model used by the patient appointment form view
     * @return the patient appointment form view
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        prepareFormModel(model, appointmentService.createEmptyPatientForm());
        return "patient/appointments/form";
    }

    /**
     * Creates an appointment for the authenticated patient.
     *
     * @param patientAppointmentFormDto the submitted patient appointment form data
     * @param bindingResult validation results
     * @param principal the authenticated user principal
     * @param model the model used when validation fails
     * @param redirectAttributes redirect attributes used after successful creation
     * @return a redirect or the patient appointment form view
     */
    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("patientAppointmentForm") PatientAppointmentFormDto patientAppointmentFormDto,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model, patientAppointmentFormDto);
            return "patient/appointments/form";
        }

        try {
            appointmentService.createForPatient(principal.getName(), patientAppointmentFormDto);
        } catch (AppointmentSchedulingException exception) {
            bindingResult.rejectValue(exception.getFieldName(), exception.getMessageKey());
            prepareFormModel(model, patientAppointmentFormDto);
            return "patient/appointments/form";
        }

        redirectAttributes.addFlashAttribute("successMessageKey", "patient.appointments.message.created");

        return "redirect:/patient/appointments";
    }

    private void prepareFormModel(Model model, PatientAppointmentFormDto patientAppointmentFormDto) {
        model.addAttribute("patientAppointmentForm", patientAppointmentFormDto);
        model.addAttribute("doctorOptions", appointmentService.findDoctorOptions());
    }
}