package hr.algebra.jwdhealthcare.dto.form;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Patient appointment booking form data is transferred between Thymeleaf forms and MVC controllers.
 */
@Getter
@Setter
@NoArgsConstructor
public class PatientAppointmentFormDto {

    @NotNull(message = "{validation.appointment.doctor.required}")
    private Integer doctorId;

    @Size(max = 1000, message = "{validation.appointment.reason.size}")
    private String reason;

    @NotNull(message = "{validation.appointment.scheduledAt.required}")
    @Future(message = "{validation.appointment.scheduledAt.future}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime scheduledAt;
}