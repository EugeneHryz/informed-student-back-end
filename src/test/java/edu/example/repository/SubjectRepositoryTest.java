package edu.example.repository;

import edu.example.PostgresTestConfig;
import edu.example.model.Subject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = PostgresTestConfig.Initializer.class)
public class SubjectRepositoryTest {

    @Autowired
    SubjectRepository subjectRepository;

    @BeforeEach
    @AfterEach
    public void clear() {
        subjectRepository.deleteAll();
    }

    @Test
    public void createSubject() {
        // when
        var newSubject = subjectRepository.save(new Subject(0L, "physics", 3));

        // then
        assertEquals("physics", newSubject.getName());
        assertEquals(3, newSubject.getCourse());
    }

}
