package edu.example;

import edu.example.config.MinioTestConfig;
import edu.example.config.PostgresTestConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = {
        MinioTestConfig.Initializer.class,
        PostgresTestConfig.Initializer.class})
@AutoConfigureMockMvc
public class TestContext {

}
