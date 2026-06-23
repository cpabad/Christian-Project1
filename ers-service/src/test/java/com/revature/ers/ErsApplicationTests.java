package com.revature.ers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: boots the full Spring context (web + datasource + JPA). If this passes,
 * the foundation compiles, the beans wire, and the service connects to PostgreSQL.
 */
@SpringBootTest
class ErsApplicationTests {

    @Test
    void contextLoads() {
    }
}
