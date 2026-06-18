package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.form.AppointmentFormDto;
import hr.algebra.jwdhealthcare.exception.AppointmentSchedulingException;
import hr.algebra.jwdhealthcare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Administrator appointment pages are handled through MVC endpoints.
 */
@Controller
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Displays all appointments for administrator review.
     *
     * @param model the model used by the appointment list view
     * @return the appointment list view
     */
    @GetMapping("/admin/appointments")
    public String list(Model model) {
        model.addAttribute("appointments", appointmentService.findAllForAdminView());
        return "admin/appointments/list";
    }

    /**
     * Displays the appointment creation form.
     *
     * @param model the model used by the appointment form view
     * @return the appointment form view
     */
    @GetMapping("/admin/appointments/create")
    public String createForm(Model model) {
        prepareFormModel(model, appointmentService.createEmptyForm(), false);
        return "admin/appointments/form";
    }

    /**
     * Creates an appointment from submitted form data.
     *
     * @param appointmentFormDto the submitted appointment form data
     * @param bindingResult validation results
     * @param model the model used when validation fails
     * @param redirectAttributes redirect attributes used after successful creation
     * @return a redirect or the appointment form view
     */
    @PostMapping("/admin/appointments/create")
    public String create(
            @Valid @ModelAttribute("appointmentForm") AppointmentFormDto appointmentFormDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model, appointmentFormDto, false);
            return "admin/appointments/form";
        }

        try {
            appointmentService.create(appointmentFormDto);
        } catch (AppointmentSchedulingException exception) {
            bindingResult.rejectValue(exception.getFieldName(), exception.getMessageKey());
            prepareFormModel(model, appointmentFormDto, false);
            return "admin/appointments/form";
        }

        redirectAttributes.addFlashAttribute("successMessageKey", "appointments.message.created");

        return "redirect:/admin/appointments";
    }

    /**
     * Displays the appointment edit form.
     *
     * @param id the appointment identifier
     * @param model the model used by the appointment form view
     * @return the appointment form view
     */
    @GetMapping("/admin/appointments/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        prepareFormModel(model, appointmentService.findFormForEdit(id), true);
        return "admin/appointments/form";
    }

    /**
     * Updates an appointment from submitted form data.
     *
     * @param id the appointment identifier
     * @param appointmentFormDto the submitted appointment form data
     * @param bindingResult validation results
     * @param model the model used when validation fails
     * @param redirectAttributes redirect attributes used after successful update
     * @return a redirect or the appointment form view
     */
    @PostMapping("/admin/appointments/edit/{id}")
    public String update(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute("appointmentForm") AppointmentFormDto appointmentFormDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        appointmentFormDto.setIdAppointment(id);

        if (bindingResult.hasErrors()) {
            prepareFormModel(model, appointmentFormDto, true);
            return "admin/appointments/form";
        }

        try {
            appointmentService.update(id, appointmentFormDto);
        } catch (AppointmentSchedulingException exception) {
            bindingResult.rejectValue(exception.getFieldName(), exception.getMessageKey());
            prepareFormModel(model, appointmentFormDto, true);
            return "admin/appointments/form";
        }

        redirectAttributes.addFlashAttribute("successMessageKey", "appointments.message.updated");

        return "redirect:/admin/appointments";
    }

    /**
     * Cancels an appointment without deleting it.
     *
     * @param id the appointment identifier
     * @param redirectAttributes redirect attributes used after cancellation
     * @return a redirect to the appointment list
     */
    @PostMapping("/admin/appointments/cancel/{id}")
    public String cancel(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes
    ) {
        appointmentService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessageKey", "appointments.message.cancelled");

        return "redirect:/admin/appointments";
    }

    private void prepareFormModel(Model model, AppointmentFormDto appointmentFormDto, boolean editMode) {
        model.addAttribute("appointmentForm", appointmentFormDto);
        model.addAttribute("doctorOptions", appointmentService.findDoctorOptions());
        model.addAttribute("patientOptions", appointmentService.findPatientOptions());
        model.addAttribute("statusOptions", appointmentService.findStatusOptions());
        model.addAttribute("editMode", editMode);
    }
}