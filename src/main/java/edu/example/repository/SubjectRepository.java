package edu.example.repository;

import edu.example.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findSubjectsByCourse(Integer course);

    boolean existsByNameAndCourse(String name, Integer course);

}
