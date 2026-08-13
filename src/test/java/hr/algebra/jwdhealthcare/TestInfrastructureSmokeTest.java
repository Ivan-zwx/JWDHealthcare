package hr.algebra.jwdhealthcare;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test infrastructure is verified without loading the Spring application context.
 */
class TestInfrastructureSmokeTest {

    /**
     * Confirms that JUnit tests are discovered and executed.
     */
    @Test
    void junitTestRunnerExecutesTests() {
        assertTrue(true);
    }
}