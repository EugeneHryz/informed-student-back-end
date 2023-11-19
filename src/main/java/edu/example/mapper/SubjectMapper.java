package edu.example.mapper;

import edu.example.dto.subject.SubjectResponseDto;
import edu.example.model.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    SubjectResponseDto toSubjectResponseDto(Subject subject);
}
