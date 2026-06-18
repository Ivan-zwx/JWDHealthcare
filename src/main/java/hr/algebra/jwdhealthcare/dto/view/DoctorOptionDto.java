package hr.algebra.jwdhealthcare.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Doctor dropdown option data is transferred to appointment forms.
 */
@Getter
@AllArgsConstructor
public class DoctorOptionDto {

    private final Integer idDoctor;
    private final String displayName;
}