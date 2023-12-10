package edu.example.dto.user;

import edu.example.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRequestDto extends PageDto {

    private Boolean isBanned;
}
