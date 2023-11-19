package edu.example.controller;

import edu.example.dto.PageResponse;
import edu.example.dto.folder.CreateFolderRequestDto;
import edu.example.dto.folder.FolderBySubjectRequestDto;
import edu.example.dto.folder.FolderResponseDto;
import edu.example.mapper.FolderMapper;
import edu.example.model.Folder;
import edu.example.service.FolderService;
import edu.example.util.FolderType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("/filterBySubject")
    @Operation(description = "Receive folders by subject")
    public PageResponse<FolderResponseDto> findFoldersByCourse(@RequestBody @Valid FolderBySubjectRequestDto filter) {
        var result = folderService.getFoldersBySubject(filter.getPageNumber(), filter.getPageSize(), filter.getSubjectId());

        var response = new PageResponse<FolderResponseDto>();
        response.setPageSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        response.setContent(result.getContent().stream().map(folderMapper::toFolderResponseDto).toList());

        return response;
    }
}