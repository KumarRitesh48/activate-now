package com.zenda.activatenow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the full Spring application context wires up correctly
 * (all beans, repositories, and JPA entity mappings) against the H2 profile.
 * Catches configuration/wiring mistakes that unit tests alone wouldn't.
 */
@SpringBootTest
@ActiveProfiles("h2")
class ActivateNowApplicationTests {

    @Test
    void contextLoads() {
        // Intentionally empty - if the context fails to start, this test fails.
    }
}
