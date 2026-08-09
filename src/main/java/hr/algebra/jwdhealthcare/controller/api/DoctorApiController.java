package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.dto.view.DoctorAppointmentViewDto;
import hr.algebra.jwdhealthcare.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Doctor REST API endpoints are handled.
 */
@RestController
@RequiredArgsConstructor
public class DoctorApiController {

    private final DoctorService doctorService;

    /**
     * Displays the schedule for the authenticated doctor.
     *
     * @param authentication the current API authentication
     * @return doctor appointment schedule data
     */
    @GetMapping("/api/doctor/schedule")
    public List<DoctorAppointmentViewDto> schedule(Authentication authentication) {
        return doctorService.findScheduleForDoctor(authentication.getName());
    }
}