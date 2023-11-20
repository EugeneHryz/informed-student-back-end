package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.Folder;
import edu.example.model.FolderType;
import edu.example.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final SubjectService subjectService;

    public List<Folder> getFoldersBySubject(long subjectId) {
        return folderRepository.findBySubjectId(subjectId);
    }

    public Folder getFolder(Long id) {
        return folderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Folder not found"));
    }

    public List<FolderType> getFolderTypes() {
        return Arrays.asList(FolderType.values());
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