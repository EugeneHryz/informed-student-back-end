package edu.example.web.controller;

import edu.example.dto.subject.CreateSubjectRequestDto;
import edu.example.dto.subject.SubjectResponseDto;
import edu.example.mapper.SubjectMapper;
import edu.example.model.Subject;
import edu.example.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
@Tag(name = "Subject", description = "work with subject")
public class SubjectController {

    private final SubjectService subjectService;
    private final SubjectMapper subjectMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('MODERATOR')")
    @Operation(description = "Create subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "409", description = "Duplicate subject"),
            @ApiResponse(responseCode = "400", description = "Parsing/validation error")
    })
    public SubjectResponseDto create(@RequestBody @Valid CreateSubjectRequestDto createSubjectRequestDto) {
        Subject subject = subjectService.createSubject(
                createSubjectRequestDto.getName(),
                createSubjectRequestDto.getCourse()
        );
        return subjectMapper.toSubjectResponseDto(subject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    @Operation(description = "Delete subject by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised")
    })
    public void delete(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }

    @GetMapping
    @Operation(description = "Receive subject by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public SubjectResponseDto get(@RequestParam Long id) {
        return subjectMapper.toSubjectResponseDto(subjectService.getSubject(id));
    }

    @GetMapping("/filterByCourse")
    @Operation(description = "Receive subjects by course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully")
    })
    public List<SubjectResponseDto> findSubjectsByCourse(@RequestParam Integer course) {
        var result = subjectService.getSubjectsByCourse(course);
        return result.stream()
                .map(subjectMapper::toSubjectResponseDto)
                .toList();
    }
}