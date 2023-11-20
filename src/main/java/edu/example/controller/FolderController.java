package edu.example.controller;

import edu.example.dto.folder.CreateFolderRequestDto;
import edu.example.dto.folder.FolderResponseDto;
import edu.example.dto.folder.FolderTypeDto;
import edu.example.mapper.FolderMapper;
import edu.example.model.Folder;
import edu.example.model.FolderType;
import edu.example.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
@Tag(name = "Folder", description = "work with folder")
public class FolderController {

    private final FolderService folderService;
    private final FolderMapper folderMapper;

    @PostMapping
    @Operation(description = "Create folder")
    public FolderResponseDto create(@RequestBody @Valid CreateFolderRequestDto createFolderRequestDto) {
        Folder folder = folderService.createFolder(
                createFolderRequestDto.getSubjectId(),
                FolderType.valueOf(createFolderRequestDto.getType())
        );
        return folderMapper.toFolderResponseDto(folder);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete folder by id")
    public void delete(@PathVariable Long id) {
        folderService.deleteFolder(id);
    }

    @GetMapping
    @Operation(description = "Receive folder by id")
    public FolderResponseDto get(@RequestParam Long id) {
        return folderMapper.toFolderResponseDto(folderService.getFolder(id));
    }

    @GetMapping("/types")
    @Operation(description = "Receive existing types")
    public List<FolderTypeDto> getFolderTypes() {
        return folderService.getFolderTypes().stream()
                .map(folderMapper::toFolderTypeDto)
                .toList();
    }

    @GetMapping("/filterBySubject")
    @Operation(description = "Receive folders by subject")
    public List<FolderResponseDto> getFoldersBySubject(@RequestParam Long subjectId) {
        var result = folderService.getFoldersBySubject(subjectId);
        return result.stream()
                .map(folderMapper::toFolderResponseDto)
                .toList();
    }
}