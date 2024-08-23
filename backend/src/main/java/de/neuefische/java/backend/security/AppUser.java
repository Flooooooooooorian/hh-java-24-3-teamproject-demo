package de.neuefische.java.backend.security;

import de.neuefische.java.backend.todo.Todo;

import java.util.List;

public record AppUser(
        String id,
        String username,
        String imageUrl,
        String role
) {
}
