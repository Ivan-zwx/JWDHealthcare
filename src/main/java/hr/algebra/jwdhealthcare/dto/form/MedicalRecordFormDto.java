package hr.algebra.jwdhealthcare.dto.form;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Medical record form data is transferred between Thymeleaf forms and MVC controllers.
 */
@Getter
@Setter
@NoArgsConstructor
public class MedicalRecordFormDto {

    private Integer idMedicalRecord;

    @Size(max = 1000, message = "{validation.medicalRecord.diagnosis.size}")
    private String diagnosis;

    @Size(max = 1000, message = "{validation.medicalRecord.treatment.size}")
    private String treatment;

    @Size(max = 1000, message = "{validation.medicalRecord.notes.size}")
    private String notes;
}