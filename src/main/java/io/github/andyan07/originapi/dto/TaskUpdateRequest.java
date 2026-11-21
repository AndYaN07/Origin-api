package io.github.andyan07.originapi.dto;

import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.model.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Datos para actualizar una tarea completa (PUT): se envían todos los campos.
 * La fecha límite no se valida contra hoy para poder editar tareas ya vencidas.
 */
public record TaskUpdateRequest(

        @Schema(example = "Preparar entrevista técnica")
        @NotBlank(message = "El título es obligatorio")
        @Size(max = Task.TITLE_MAX_LENGTH, message = "El título no puede superar 100 caracteres")
        String title,

        @Schema(example = "Repasar Spring Boot, JPA y SQL")
        @Size(max = Task.DESCRIPTION_MAX_LENGTH, message = "La descripción no puede superar 500 caracteres")
        String description,

        @Schema(example = "true")
        @NotNull(message = "El campo completed es obligatorio")
        Boolean completed,

        @Schema(example = "ALTA")
        @NotNull(message = "La prioridad es obligatoria")
        Priority priority,

        @Schema(description = "Opcional", example = "2026-12-15")
        LocalDate dueDate
) {
}
