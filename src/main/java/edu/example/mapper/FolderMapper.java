package edu.example.mapper;

import edu.example.dto.folder.FolderResponseDto;
import edu.example.model.Folder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class})
public interface FolderMapper {

    FolderResponseDto toFolderResponseDto(Folder folder);
}
