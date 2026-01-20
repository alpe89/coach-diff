package com.coachdiff;

import com.coachdiff.infrastructure.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Smoke test: verifies that the ApplicationContext starts successfully.
 *
 * <h2>What it tests</h2>
 * <p>
 * This test verifies that:
 * </p>
 * <ol>
 *   <li>Spring Boot can create the ApplicationContext</li>
 *   <li>All dependencies are satisfied (no missing beans)</li>
 *   <li>Configuration is valid (no parsing errors)</li>
 *   <li>Flyway migrations run successfully</li>
 *   <li>PostgreSQL and Redis connections work</li>
 * </ol>
 *
 * <h2>Why it matters</h2>
 * <p>
 * If this test fails, there's a fundamental problem:
 * </p>
 * <ul>
 *   <li>Missing or circular dependency</li>
 *   <li>Malformed YAML configuration</li>
 *   <li>SQL migration with errors</li>
 *   <li>Bean not found</li>
 * </ul>
 *
 * <h2>@SpringBootTest</h2>
 * <p>
 * Starts the full ApplicationContext like in production.
 * It's the "heaviest" test but also the most realistic.
 * </p>
 *
 * <h2>@Import(TestContainersConfig.class)</h2>
 * <p>
 * Imports the Testcontainers configuration that starts:
 * </p>
 * <ul>
 *   <li>PostgreSQL container (with @ServiceConnection auto-config)</li>
 *   <li>Redis container</li>
 * </ul>
 *
 * <h2>Execution</h2>
 * <pre>
 * # Via Maven
 * ./mvnw test -Dtest=ApplicationTests
 *
 * # Via IDE
 * Right-click → Run 'ApplicationTests'
 * </pre>
 *
 * <h2>Prerequisites</h2>
 * <ul>
 *   <li>Docker must be running</li>
 *   <li>First run may be slow (image download)</li>
 * </ul>
 */
@SpringBootTest
@Import(TestContainersConfig.class)
class ApplicationTests {

    /**
     * Context load test.
     *
     * <p>
     * The test is empty because the real test is the {@code @SpringBootTest} annotation:
     * if the ApplicationContext doesn't start, the test fails before
     * even executing this method.
     * </p>
     *
     * <p>
     * If you see this test pass, it means:
     * </p>
     * <ul>
     *   <li>✅ Spring Boot starts</li>
     *   <li>✅ PostgreSQL container works</li>
     *   <li>✅ Redis container works</li>
     *   <li>✅ Flyway migrations executed</li>
     *   <li>✅ No missing beans</li>
     * </ul>
     */
    @Test
    void contextLoads() {
        // Test passes if we get here without exceptions
        // ApplicationContext was loaded successfully
    }
}
