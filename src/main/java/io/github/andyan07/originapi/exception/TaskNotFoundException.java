package io.github.andyan07.originapi.exception;

/** Se lanza cuando se pide una tarea que no existe. El manejador global la convierte en 404. */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("No existe ninguna tarea con id " + id);
    }
}
