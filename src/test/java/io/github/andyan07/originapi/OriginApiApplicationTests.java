package io.github.andyan07.originapi;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Prueba de extremo a extremo: aplicación completa con base de datos H2 en memoria. */
@SpringBootTest
@AutoConfigureMockMvc
class OriginApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cicloCompletoDeUnaTarea() throws Exception {
        String body = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Ciclo completo\",\"priority\":\"ALTA\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(body, "$.id");

        mockMvc.perform(get("/tasks").param("priority", "ALTA").param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Ciclo completo"));

        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Ciclo completo\",\"completed\":true,\"priority\":\"BAJA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.priority").value("BAJA"));

        mockMvc.perform(delete("/tasks/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/tasks/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void ordenarPorCampoInexistente_devuelve400() throws Exception {
        mockMvc.perform(get("/tasks").param("sort", "noExiste"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void swaggerEstaDisponible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Origin API")));
    }
}
