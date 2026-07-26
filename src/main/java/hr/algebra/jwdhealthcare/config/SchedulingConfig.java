package hr.algebra.jwdhealthcare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduled task execution is enabled for application-managed background jobs.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}