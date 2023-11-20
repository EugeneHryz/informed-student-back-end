package edu.example.mapper;

import edu.example.dto.folder.FolderResponseDto;
import edu.example.dto.folder.FolderTypeDto;
import edu.example.model.Folder;
import edu.example.model.FolderType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class})
public interface FolderMapper {

    FolderResponseDto toFolderResponseDto(Folder folder);

    default FolderTypeDto toFolderTypeDto(FolderType type) {
        return new FolderTypeDto(type.name(), type.getDisplayName());
    }
}
