package edu.example.mapper;

import edu.example.dto.fileModel.FileModelResponseDto;
import edu.example.model.FileModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileModelMapper {

    FileModelResponseDto toFileModelResponseDto(FileModel file);

    List<FileModelResponseDto> toFileModelResponseDto(List<FileModel> files);
}
