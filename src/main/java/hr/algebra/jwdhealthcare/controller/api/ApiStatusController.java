package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.dto.api.ApiStatusResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple secured API status endpoints are handled for role-based JWT testing.
 */
@RestController
public class ApiStatusController {

    /**
     * Displays administrator API access status.
     *
     * @param authentication the current API authentication
     * @return the API status response
     */
    @GetMapping("/api/admin/status")
    public ApiStatusResponseDto adminStatus(Authentication authentication) {
        return new ApiStatusResponseDto(
                "admin",
                authentication.getName(),
                "Administrator API access is available."
        );
    }

    /**
     * Displays doctor API access status.
     *
     * @param authentication the current API authentication
     * @return the API status response
     */
    @GetMapping("/api/doctor/status")
    public ApiStatusResponseDto doctorStatus(Authentication authentication) {
        return new ApiStatusResponseDto(
                "doctor",
                authentication.getName(),
                "Doctor API access is available."
        );
    }

    /**
     * Displays patient API access status.
     *
     * @param authentication the current API authentication
     * @return the API status response
     */
    @GetMapping("/api/patient/status")
    public ApiStatusResponseDto patientStatus(Authentication authentication) {
        return new ApiStatusResponseDto(
                "patient",
                authentication.getName(),
                "Patient API access is available."
        );
    }
}