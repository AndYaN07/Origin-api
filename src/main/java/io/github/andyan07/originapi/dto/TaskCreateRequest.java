package io.github.andyan07.originapi.dto;

import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.model.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Datos para crear una tarea. Toda tarea nueva empieza como no completada. */
public record TaskCreateRequest(

        @Schema(example = "Preparar entrevista técnica")
        @NotBlank(message = "El título es obligatorio")
        @Size(max = Task.TITLE_MAX_LENGTH, message = "El título no puede superar 100 caracteres")
        String title,

        @Schema(example = "Repasar Spring Boot, JPA y SQL")
        @Size(max = Task.DESCRIPTION_MAX_LENGTH, message = "La descripción no puede superar 500 caracteres")
        String description,

        @Schema(description = "Opcional. Si no se indica, se usa MEDIA", example = "ALTA")
        Priority priority,

        @Schema(description = "Opcional. No puede ser anterior a hoy", example = "2026-12-15")
        @FutureOrPresent(message = "La fecha límite no puede ser anterior a hoy")
        LocalDate dueDate
) {
}
