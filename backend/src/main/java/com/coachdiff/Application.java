package com.coachdiff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the CoachDiff.ai application.
 *
 * <h2>What @SpringBootApplication does</h2>
 * <p>
 * It's a meta-annotation that includes:
 * </p>
 * <ul>
 *   <li>{@code @Configuration}: This class can define beans with @Bean</li>
 *   <li>{@code @EnableAutoConfiguration}: Spring Boot automatically configures
 *       dependencies based on the classpath (e.g., finds PostgreSQL driver →
 *       configures DataSource)</li>
 *   <li>{@code @ComponentScan}: Scans for @Component, @Service, @Repository, @Controller
 *       in this package and all sub-packages</li>
 * </ul>
 *
 * <h2>Virtual Threads (Java 21)</h2>
 * <p>
 * Spring Boot 4.0 enables Virtual Threads by default when configured in
 * application.yml ({@code spring.threads.virtual.enabled: true}).
 * </p>
 * <p>
 * Virtual Threads are lightweight threads managed by the JVM (not the OS):
 * </p>
 * <ul>
 *   <li>Millions of virtual threads possible (vs thousands of platform threads)</li>
 *   <li>Blocking I/O (HTTP call, DB query) doesn't waste resources</li>
 *   <li>Simple synchronous code with async performance</li>
 * </ul>
 *
 * <h2>Startup</h2>
 * <pre>
 * # Via Maven
 * ./mvnw spring-boot:run
 *
 * # Via JAR
 * java -jar target/coach-diff-backend-1.0.0-SNAPSHOT.jar
 *
 * # Via Makefile
 * make backend
 * </pre>
 */
@SpringBootApplication
public class Application {

    /**
     * Main method - JVM entry point.
     *
     * <p>
     * {@code SpringApplication.run()} does:
     * </p>
     * <ol>
     *   <li>Creates ApplicationContext (Spring's IoC container)</li>
     *   <li>Runs auto-configuration</li>
     *   <li>Runs component scan</li>
     *   <li>Starts embedded Tomcat</li>
     *   <li>Runs Flyway migrations (if configured)</li>
     * </ol>
     *
     * @param args command line arguments (e.g., --server.port=9090)
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
