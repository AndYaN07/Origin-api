package io.github.andyan07.originapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.andyan07.originapi.dto.TaskCreateRequest;
import io.github.andyan07.originapi.dto.TaskResponse;
import io.github.andyan07.originapi.dto.TaskUpdateRequest;
import io.github.andyan07.originapi.exception.TaskNotFoundException;
import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.model.Task;
import io.github.andyan07.originapi.repository.TaskRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void create_aplicaValoresPorDefectoYLimpiaElTitulo() {
        when(repository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        TaskResponse response = service.create(new TaskCreateRequest("  Estudiar JPA  ", null, null, null));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository).save(captor.capture());
        Task saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Estudiar JPA");
        assertThat(saved.isCompleted()).isFalse();
        assertThat(saved.getPriority()).isEqualTo(Priority.MEDIA);
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void create_respetaLaPrioridadIndicada() {
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = service.create(new TaskCreateRequest("Urgente", null, Priority.ALTA, null));

        assertThat(response.priority()).isEqualTo(Priority.ALTA);
    }

    @Test
    void findById_lanzaExcepcionSiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_modificaTodosLosCampos() {
        Task existing = new Task();
        existing.setId(5L);
        existing.setTitle("Viejo");
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(existing)).thenReturn(existing);
        LocalDate dueDate = LocalDate.of(2026, 12, 31);

        TaskResponse response = service.update(5L,
                new TaskUpdateRequest("Nuevo", "Descripción", true, Priority.BAJA, dueDate));

        assertThat(response.title()).isEqualTo("Nuevo");
        assertThat(response.description()).isEqualTo("Descripción");
        assertThat(response.completed()).isTrue();
        assertThat(response.priority()).isEqualTo(Priority.BAJA);
        assertThat(response.dueDate()).isEqualTo(dueDate);
    }

    @Test
    void delete_noBorraNadaSiLaTareaNoExiste() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(7L)).isInstanceOf(TaskNotFoundException.class);
        verify(repository, never()).delete(any(Task.class));
    }
}
