package edu.example.repository;

import edu.example.model.Folder;
import edu.example.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    Set<Folder> getFoldersBySubject(Subject subject);

    Page<Folder> findBySubjectId(Long subjectId, Pageable pageable);
}
