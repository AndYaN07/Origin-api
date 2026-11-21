package io.github.andyan07.originapi.controller;

import io.github.andyan07.originapi.dto.PageResponse;
import io.github.andyan07.originapi.dto.TaskCreateRequest;
import io.github.andyan07.originapi.dto.TaskResponse;
import io.github.andyan07.originapi.dto.TaskUpdateRequest;
import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Endpoints REST de tareas. Solo traduce HTTP a llamadas al servicio. */
@RestController
@RequestMapping("/tasks")
@Tag(name = "Tareas", description = "Gestión de tareas")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "Listar tareas",
            description = "Paginado y ordenable. Ejemplo: ?completed=false&priority=ALTA&page=0&size=10&sort=dueDate,asc")
    @GetMapping
    public PageResponse<TaskResponse> findAll(
            @Parameter(description = "Filtrar por estado") @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Filtrar por prioridad") @RequestParam(required = false) Priority priority,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.findAll(completed, priority, pageable);
    }

    @Operation(summary = "Obtener una tarea por id")
    @ApiResponse(responseCode = "404", description = "La tarea no existe")
    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Crear una tarea")
    @ApiResponse(responseCode = "201", description = "Tarea creada")
    @ApiResponse(responseCode = "400", description = "Datos no válidos")
    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Actualizar una tarea completa")
    @ApiResponse(responseCode = "400", description = "Datos no válidos")
    @ApiResponse(responseCode = "404", description = "La tarea no existe")
    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar una tarea")
    @ApiResponse(responseCode = "204", description = "Tarea eliminada")
    @ApiResponse(responseCode = "404", description = "La tarea no existe")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
