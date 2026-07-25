package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.form.MedicalRecordFormDto;
import hr.algebra.jwdhealthcare.service.DoctorService;
import hr.algebra.jwdhealthcare.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Doctor schedule and medical record pages are handled through MVC endpoints.
 */
@Controller
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorService doctorService;
    private final MedicalRecordService medicalRecordService;

    /**
     * Displays the schedule for the authenticated doctor.
     *
     * @param principal the authenticated user principal
     * @param model the model used by the doctor schedule view
     * @return the doctor schedule view
     */
    @GetMapping("/doctor/schedule")
    public String schedule(Principal principal, Model model) {
        model.addAttribute("appointments", doctorService.findScheduleForDoctor(principal.getName()));
        return "doctor/schedule";
    }

    /**
     * Displays the medical record form for a doctor's appointment.
     *
     * @param id the appointment identifier
     * @param principal the authenticated user principal
     * @param model the model used by the medical record form view
     * @return the medical record form view
     */
    @GetMapping("/doctor/appointments/{id}/record")
    public String medicalRecordForm(
            @PathVariable("id") Integer id,
            Principal principal,
            Model model
    ) {
        prepareMedicalRecordFormModel(
                model,
                principal.getName(),
                id,
                medicalRecordService.findFormForDoctorAppointment(principal.getName(), id)
        );

        return "doctor/medical-record-form";
    }

    /**
     * Saves the medical record for a doctor's appointment.
     *
     * @param id the appointment identifier
     * @param medicalRecordFormDto the submitted medical record form data
     * @param bindingResult validation results
     * @param principal the authenticated user principal
     * @param model the model used when validation fails
     * @param redirectAttributes redirect attributes used after successful saving
     * @return a redirect or the medical record form view
     */
    @PostMapping("/doctor/appointments/{id}/record")
    public String saveMedicalRecord(
            @PathVariable("id") Integer id,
            @Valid MedicalRecordFormDto medicalRecordFormDto,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareMedicalRecordFormModel(model, principal.getName(), id, medicalRecordFormDto);
            return "doctor/medical-record-form";
        }

        medicalRecordService.saveForDoctorAppointment(principal.getName(), id, medicalRecordFormDto);
        redirectAttributes.addFlashAttribute("successMessageKey", "doctor.schedule.message.recordSaved");

        return "redirect:/doctor/schedule";
    }

    private void prepareMedicalRecordFormModel(
            Model model,
            String username,
            Integer idAppointment,
            MedicalRecordFormDto medicalRecordFormDto
    ) {
        model.addAttribute("appointment", doctorService.findAppointmentForDoctorView(username, idAppointment));
        model.addAttribute("medicalRecordForm", medicalRecordFormDto);
    }
}