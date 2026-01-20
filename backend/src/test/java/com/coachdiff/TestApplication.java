package com.coachdiff;

import com.coachdiff.infrastructure.config.TestContainersConfig;
import org.springframework.boot.SpringApplication;

/**
 * Entry point for local development WITH Testcontainers.
 *
 * <h2>When to use it</h2>
 * <p>
 * Use this class instead of {@link Application} when:
 * </p>
 * <ul>
 *   <li>You don't want to start Docker Compose manually</li>
 *   <li>You want a clean environment on each startup</li>
 *   <li>You're debugging and want to isolate data</li>
 * </ul>
 *
 * <h2>How it works</h2>
 * <p>
 * {@code SpringApplication.from(...).with(...)} is a Spring Boot 3.1+ feature
 * that allows adding configurations at startup:
 * </p>
 * <ol>
 *   <li>Starts from normal configuration ({@link Application})</li>
 *   <li>Adds {@link TestContainersConfig} which starts PostgreSQL and Redis</li>
 *   <li>Containers stay alive as long as the app is running</li>
 * </ol>
 *
 * <h2>Benefits vs Docker Compose</h2>
 * <table>
 *   <tr><th>Aspect</th><th>Docker Compose</th><th>TestApplication</th></tr>
 *   <tr><td>Setup</td><td>{@code make start}</td><td>Automatic</td></tr>
 *   <tr><td>Data</td><td>Persistent</td><td>Clean each startup</td></tr>
 *   <tr><td>Resources</td><td>Always active</td><td>Only when needed</td></tr>
 * </table>
 *
 * <h2>Execution</h2>
 *
 * <h3>Via Maven</h3>
 * <pre>
 * ./mvnw spring-boot:test-run
 * </pre>
 *
 * <h3>Via IntelliJ IDEA</h3>
 * <ol>
 *   <li>Open this class</li>
 *   <li>Right-click on the main method</li>
 *   <li>Select "Run 'TestApplication.main()'"</li>
 * </ol>
 *
 * <h2>Note</h2>
 * <p>
 * Containers stop when you close the application.
 * Data is lost (no persistent volume).
 * </p>
 */
public class TestApplication {

    /**
     * Main method for startup with Testcontainers.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(TestContainersConfig.class)
                .run(args);
    }
}
