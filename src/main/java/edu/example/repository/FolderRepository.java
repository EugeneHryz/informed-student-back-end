package edu.example.repository;

import edu.example.model.Folder;
import edu.example.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    Set<Folder> getFoldersBySubject(Subject subject);

    List<Folder> findBySubjectId(Long subjectId);
}
