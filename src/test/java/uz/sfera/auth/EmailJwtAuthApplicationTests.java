package uz.sfera.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailJwtAuthApplicationTests {

    @Test
    void contextLoads() {
        // Application context test. Requires a PostgreSQL test database and SMTP settings
        // only when email endpoints are invoked.
    }
}
