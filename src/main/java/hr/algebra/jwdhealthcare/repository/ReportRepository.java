package hr.algebra.jwdhealthcare.repository;

import hr.algebra.jwdhealthcare.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access operations are provided for generated reports.
 */
public interface ReportRepository extends JpaRepository<Report, Integer> {
}