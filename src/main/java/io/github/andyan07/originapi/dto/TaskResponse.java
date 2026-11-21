package io.github.andyan07.originapi.dto;

import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.model.Task;
import java.time.Instant;
import java.time.LocalDate;

/** Tarea tal y como la devuelve la API. Separa el contrato público de la entidad JPA. */
public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        Priority priority,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
