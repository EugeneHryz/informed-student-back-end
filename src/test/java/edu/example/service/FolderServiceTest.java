package edu.example.service;

import edu.example.TestContext;
import edu.example.exception.DuplicateEntityException;
import edu.example.exception.EntityNotFoundException;
import edu.example.model.FolderType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FolderServiceTest extends TestContext {

    @Autowired
    FolderService folderService;
    @Autowired
    SubjectService subjectService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void clear() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "folder", "subject");
    }

    @Test
    void getFolderTypes() {
        // when
        var types = folderService.getFolderTypes();

        // then
        assertEquals(FolderType.values().length, types.size());
    }

    @ParameterizedTest
    @EnumSource(value = FolderType.class, names = {"TEST", "NOTES", "LITERATURE"})
    void createFolder(FolderType folderType) {
        // given
        var subject = subjectService.createSubject("Physics", 1);

        // when
        var folder = folderService.createFolder(subject.getId(), folderType);

        // then
        assertEquals(folderType, folder.getType());
        assertEquals(subject.getId(), folder.getSubject().getId());
    }

    @Test
    void createFolderDuplicate() {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        // when
        assertThrows(DuplicateEntityException.class, () ->
                folderService.createFolder(subject.getId(), FolderType.NOTES));
    }

    @ParameterizedTest
    @EnumSource(value = FolderType.class, names = {"TEST", "NOTES", "LITERATURE"})
    void getFolder(FolderType folderType) {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder_id = folderService.createFolder(subject.getId(), folderType).getId();

        // when
        var folder = folderService.getFolder(folder_id);

        // then
        assertEquals(folderType, folder.getType());
        assertEquals(subject.getId(), folder.getSubject().getId());
    }

    @Test
    void deleteFolder() {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder_id = folderService.createFolder(subject.getId(), FolderType.NOTES).getId();

        // when
        folderService.deleteFolder(folder_id);

        // then
        assertThrows(EntityNotFoundException.class, () ->
                folderService.getFolder(folder_id));
    }

    @Test
    void deleteFolderNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                folderService.deleteFolder(0L));
    }

    @ParameterizedTest
    @EnumSource(value = FolderType.class, names = {"TEST", "NOTES", "LITERATURE"})
    void updateFolder(FolderType folderType) {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder_id = folderService.createFolder(subject.getId(), FolderType.NOTES).getId();

        // when
        folderService.updateFolder(folder_id, subject.getId(), folderType);

        // then
        var folder = folderService.getFolder(folder_id);
        assertEquals(folderType, folder.getType());
        assertEquals(subject.getId(), folder.getSubject().getId());
    }

    @Test
    void fetFoldersBySubject() {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder1 = folderService.createFolder(subject.getId(), FolderType.NOTES).getId();
        var folder2 = folderService.createFolder(subject.getId(), FolderType.TEST).getId();

        // when
        var folderList = folderService.getFoldersBySubject(subject.getId());

        // then
        assertEquals(2, folderList.size());
    }

}
