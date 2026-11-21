package io.github.andyan07.originapi.repository;

import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.model.Task;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filtros reutilizables para consultar tareas.
 * Cada método devuelve null si el filtro no se indica, y Spring Data lo ignora.
 */
public final class TaskSpecifications {

    private TaskSpecifications() {
    }

    public static Specification<Task> hasCompleted(Boolean completed) {
        return (root, query, cb) -> completed == null ? null : cb.equal(root.get("completed"), completed);
    }

    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    /** Combina todos los filtros opcionales en una sola consulta. */
    public static Specification<Task> withFilters(Boolean completed, Priority priority) {
        return Specification.allOf(hasCompleted(completed), hasPriority(priority));
    }
}
