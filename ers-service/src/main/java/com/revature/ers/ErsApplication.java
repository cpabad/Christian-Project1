package com.revature.ers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the ERS Spring Boot microservice.
 *
 * {@code @SpringBootApplication} bundles three annotations:
 *   - @Configuration       (this class can define beans)
 *   - @EnableAutoConfiguration (Boot wires Tomcat, the DataSource, JPA, etc. from the classpath)
 *   - @ComponentScan       (discovers @RestController / @Service / @Repository under this package)
 *
 * This replaces the monolith's web.xml + FrontController bootstrapping.
 */
@SpringBootApplication
public class ErsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErsApplication.class, args);
    }
}
