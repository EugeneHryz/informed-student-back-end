package edu.example.dto.post;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePostRequestDto {

    private Long folderId;

    @NotEmpty
    private String text;
}
