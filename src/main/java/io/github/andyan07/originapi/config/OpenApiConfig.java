package io.github.andyan07.originapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Datos generales que aparecen en la documentación de Swagger (/swagger-ui.html). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI originApiInfo() {
        return new OpenAPI().info(new Info()
                .title("Origin API")
                .version("1.0.0")
                .description("API REST de gestión de tareas con filtros, paginación y validación."));
    }
}
