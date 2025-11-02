package originApiMain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Clase para exception si una tarea no existe

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Integer id) {
        super("No se encuentra la tarea con ID: " + id + " ❌");
    }
}
