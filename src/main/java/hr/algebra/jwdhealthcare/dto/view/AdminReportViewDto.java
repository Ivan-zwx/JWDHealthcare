package hr.algebra.jwdhealthcare.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Generated report display data is transferred from services to administrator MVC views.
 */
@Getter
@AllArgsConstructor
public class AdminReportViewDto {

    private final Integer idReport;
    private final String title;
    private final String summary;
    private final LocalDateTime generatedAt;
}