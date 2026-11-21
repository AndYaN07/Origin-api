package io.github.andyan07.originapi.repository;

import io.github.andyan07.originapi.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Acceso a datos de tareas. Spring Data genera la implementación.
 * JpaSpecificationExecutor permite combinar filtros opcionales (ver {@link TaskSpecifications}).
 */
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
}
