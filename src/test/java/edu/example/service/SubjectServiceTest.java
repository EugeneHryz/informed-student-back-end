package edu.example.service;

import edu.example.TestContext;
import edu.example.exception.DuplicateEntityException;
import edu.example.exception.EntityNotFoundException;
import edu.example.repository.SubjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubjectServiceTest extends TestContext {

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void clear() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "subject");
    }

    @Test
    void createSubject() {
        // given
        subjectService.createSubject("Physics", 2);

        // when
        var subject = subjectService.createSubject("Physics", 1);

        // then
        assertEquals("Physics", subject.getName());
        assertEquals(1, subject.getCourse());
    }

    @Test
    void createSubjectDuplicate() {
        // given
        var subject = subjectService.createSubject("Physics", 1);

        // when
        assertThrows(DuplicateEntityException.class, () ->
                subjectService.createSubject("Physics", 1));
    }

    @Test
    void getSubject() {
        // given
        var subject_id = subjectService.createSubject("Physics", 1).getId();

        // when
        var subject = subjectService.getSubject(subject_id);

        // then
        assertThrows(DuplicateEntityException.class, () ->
                subjectService.createSubject("Physics", 1));
    }

    @Test
    void deleteSubject() {
        // given
        var subject = subjectService.createSubject("Physics", 2);

        // when
        subjectService.deleteSubject(subject.getId());

        // then
        assertThrows(EntityNotFoundException.class, () ->
                subjectService.getSubject(subject.getId()));
    }

    @Test
    void updateSubject() {
        // given
        var subject = subjectService.createSubject("Physics", 2);

        // when
        subject = subjectService.updateSubject(subject.getId(), "Maths", 3);

        // then
        assertEquals("Maths", subject.getName());
        assertEquals(3, subject.getCourse());
    }

}
