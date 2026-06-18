package hr.algebra.jwdhealthcare.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Patient dropdown option data is transferred to appointment forms.
 */
@Getter
@AllArgsConstructor
public class PatientOptionDto {

    private final Integer idPatient;
    private final String displayName;
}