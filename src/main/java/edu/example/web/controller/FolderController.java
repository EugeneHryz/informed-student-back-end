package edu.example.web.controller;

import edu.example.dto.folder.CreateFolderRequestDto;
import edu.example.dto.folder.FolderResponseDto;
import edu.example.dto.folder.FolderTypeDto;
import edu.example.mapper.FolderMapper;
import edu.example.model.Folder;
import edu.example.model.FolderType;
import edu.example.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "409", description = "Folder of this type and subject already exists")
    })
    public FolderResponseDto create(@RequestBody @Valid CreateFolderRequestDto createFolderRequestDto) {
        Folder folder = folderService.createFolder(
                createFolderRequestDto.getSubjectId(),
                FolderType.valueOf(createFolderRequestDto.getType())
        );
        return folderMapper.toFolderResponseDto(folder);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete folder by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Folder not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised")
    })
    public void delete(@PathVariable Long id) {
        folderService.deleteFolder(id);
    }

    @GetMapping
    @Operation(description = "Receive folder by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public FolderResponseDto get(@RequestParam Long id) {
        return folderMapper.toFolderResponseDto(folderService.getFolder(id));
    }

    @GetMapping("/types")
    @Operation(description = "Receive existing types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully")
    })
    public List<FolderTypeDto> getFolderTypes() {
        return folderService.getFolderTypes().stream()
                .map(folderMapper::toFolderTypeDto)
                .toList();
    }

    @GetMapping("/filterBySubject")
    @Operation(description = "Receive folders by subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully")
    })
    public List<FolderResponseDto> getFoldersBySubject(@RequestParam Long subjectId) {
        var result = folderService.getFoldersBySubject(subjectId);
        return result.stream()
                .map(folderMapper::toFolderResponseDto)
                .toList();
    }
}