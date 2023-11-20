package edu.example.controller;

import edu.example.dto.subject.CreateSubjectRequestDto;
import edu.example.dto.subject.SubjectResponseDto;
import edu.example.mapper.SubjectMapper;
import edu.example.model.Subject;
import edu.example.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @Operation(description = "Create subject")
    public SubjectResponseDto create(@RequestBody @Valid CreateSubjectRequestDto createSubjectRequestDto) {
        Subject subject = subjectService.createSubject(
                createSubjectRequestDto.getName(),
                createSubjectRequestDto.getCourse()
        );
        return subjectMapper.toSubjectResponseDto(subject);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete subject by id")
    public void delete(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }

    @GetMapping
    @Operation(description = "Receive subject by id")
    public SubjectResponseDto get(@RequestParam Long id) {
        return subjectMapper.toSubjectResponseDto(subjectService.getSubject(id));
    }

    @GetMapping("/filterByCourse")
    @Operation(description = "Receive subjects by course")
    public List<SubjectResponseDto> findSubjectsByCourse(@RequestParam Integer course) {
        var result = subjectService.getSubjectsByCourse(course);
        return result.stream()
                .map(subjectMapper::toSubjectResponseDto)
                .toList();
    }
}