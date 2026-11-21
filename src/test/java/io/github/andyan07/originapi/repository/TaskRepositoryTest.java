package io.github.andyan07.originapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.model.Task;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        repository.save(task("A", false, Priority.ALTA, LocalDate.of(2026, 12, 3)));
        repository.save(task("B", true, Priority.ALTA, LocalDate.of(2026, 12, 1)));
        repository.save(task("C", false, Priority.BAJA, LocalDate.of(2026, 12, 2)));
    }

    @Test
    void guardar_rellenaLasFechasDeAuditoria() {
        Task saved = repository.saveAndFlush(task("D", false, Priority.MEDIA, null));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void sinFiltros_devuelveTodas() {
        Page<Task> page = repository.findAll(TaskSpecifications.withFilters(null, null), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void filtros_seCombinan() {
        Page<Task> page = repository.findAll(
                TaskSpecifications.withFilters(false, Priority.ALTA), PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(Task::getTitle).containsExactly("A");
    }

    @Test
    void paginacionYOrdenacion_funcionan() {
        Page<Task> page = repository.findAll(
                TaskSpecifications.withFilters(null, null),
                PageRequest.of(0, 2, Sort.by("dueDate").ascending()));

        assertThat(page.getContent()).extracting(Task::getTitle).containsExactly("B", "C");
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    private static Task task(String title, boolean completed, Priority priority, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setCompleted(completed);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        return task;
    }
}
