package io.github.andyan07.originapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.andyan07.originapi.dto.TaskCreateRequest;
import io.github.andyan07.originapi.dto.TaskResponse;
import io.github.andyan07.originapi.exception.TaskNotFoundException;
import io.github.andyan07.originapi.model.Priority;
import io.github.andyan07.originapi.service.TaskService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService service;

    @Test
    void post_conDatosValidos_devuelve201YCabeceraLocation() throws Exception {
        Instant now = Instant.now();
        when(service.create(any(TaskCreateRequest.class))).thenReturn(
                new TaskResponse(1L, "Estudiar", null, false, Priority.MEDIA, null, now, now));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Estudiar\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/tasks/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.priority").value("MEDIA"));
    }

    @Test
    void post_sinTitulo_devuelve400ConElErrorDelCampo() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").value("El título es obligatorio"));

        verifyNoInteractions(service);
    }

    @Test
    void post_conFechaLimitePasada_devuelve400() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Tarde\",\"dueDate\":\"2000-01-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.dueDate").exists());
    }

    @Test
    void post_conPrioridadInexistente_devuelve400() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\",\"priority\":\"URGENTE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_tareaInexistente_devuelve404() throws Exception {
        when(service.findById(42L)).thenThrow(new TaskNotFoundException(42L));

        mockMvc.perform(get("/tasks/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/tasks/42"));
    }

    @Test
    void get_conIdNoNumerico_devuelve400() throws Exception {
        mockMvc.perform(get("/tasks/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_conFiltroDePrioridadInvalido_devuelve400() throws Exception {
        mockMvc.perform(get("/tasks").param("priority", "URGENTE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_devuelve204() throws Exception {
        mockMvc.perform(delete("/tasks/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_tareaInexistente_devuelve404() throws Exception {
        doThrow(new TaskNotFoundException(8L)).when(service).delete(eq(8L));

        mockMvc.perform(delete("/tasks/8"))
                .andExpect(status().isNotFound());
    }
}
