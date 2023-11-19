package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.Folder;
import edu.example.util.FolderType;
import edu.example.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final SubjectService subjectService;

    public Page<Folder> getFoldersBySubject(int pageNumber, int pageSize, long subjectId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("id")
        ));

        return folderRepository.findBySubjectId(subjectId, pageable);
    }

    public Folder getFolder(Long id) {
        return folderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Folder not found"));
    }

    public Folder createFolder(Long subjectId, FolderType folderType) {
        var folder = new Folder();
        folder.setSubject(subjectService.getSubject(subjectId));
        folder.setType(folderType);
        return folderRepository.save(folder);
    }

    public Folder updateFolder(Long id, Long subjectId, FolderType folderType) {
        var folder = new Folder();
        folder.setId(id);
        folder.setSubject(subjectService.getSubject(subjectId));
        folder.setType(folderType);
        return folderRepository.save(folder);
    }

    public void deleteFolder(Long id) {
        folderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Folder not found"));
        folderRepository.deleteById(id);
    }
}