package edu.example.model;

import lombok.Getter;
import java.util.Set;

@Getter
public enum Role {
    USER,
    MODERATOR,
    ADMIN;

    private Set<Role> roles;

    static {
        USER.roles = Set.of(USER);
        MODERATOR.roles = Set.of(USER, MODERATOR);
        ADMIN.roles = Set.of(USER, MODERATOR, ADMIN);
    }
}
