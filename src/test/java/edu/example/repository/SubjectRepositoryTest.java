package edu.example.repository;

import edu.example.TestContext;
import edu.example.model.Subject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubjectRepositoryTest extends TestContext {

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
