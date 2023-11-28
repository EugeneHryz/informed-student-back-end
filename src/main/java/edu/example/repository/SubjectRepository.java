package edu.example.repository;

import edu.example.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findSubjectsByCourse(Integer course);

    Optional<Subject> findByName(String name);
}
