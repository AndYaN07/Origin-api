package io.github.andyan07.originapi.service;

import io.github.andyan07.originapi.dto.PageResponse;
import io.github.andyan07.originapi.dto.TaskCreateRequest;
import io.github.andyan07.originapi.dto.TaskResponse;
import io.github.andyan07.originapi.dto.TaskUpdateRequest;
import io.github.andyan07.originapi.exception.TaskNotFoundException;
import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.model.Task;
import io.github.andyan07.originapi.repository.TaskRepository;
import io.github.andyan07.originapi.repository.TaskSpecifications;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lógica de negocio de las tareas. */
@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TaskResponse create(TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setCompleted(false);
        task.setPriority(request.priority() != null ? request.priority() : Priority.MEDIA);
        task.setDueDate(request.dueDate());
        return TaskResponse.from(repository.save(task));
    }

    /** Lista tareas paginadas. Los filtros son opcionales: si llegan a null no se aplican. */
    public PageResponse<TaskResponse> findAll(Boolean completed, Priority priority, Pageable pageable) {
        return PageResponse.from(
                repository.findAll(TaskSpecifications.withFilters(completed, priority), pageable)
                        .map(TaskResponse::from));
    }

    public TaskResponse findById(Long id) {
        return TaskResponse.from(getTaskOrThrow(id));
    }

    @Transactional
    public TaskResponse update(Long id, TaskUpdateRequest request) {
        Task task = getTaskOrThrow(id);
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setCompleted(request.completed());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        // saveAndFlush para que updatedAt se actualice antes de construir la respuesta
        return TaskResponse.from(repository.saveAndFlush(task));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getTaskOrThrow(id));
    }

    private Task getTaskOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
