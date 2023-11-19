package edu.example.repository;

import edu.example.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Page<Subject> findSubjectsByCourse(Integer course, Pageable pageable);
}
