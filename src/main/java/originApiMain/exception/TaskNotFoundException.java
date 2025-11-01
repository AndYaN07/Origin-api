package originApiMain.exception;


//Clase para exception si una tarea no existe

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException(int id){
        super("No se encuentra la tarea con ID: " + id + " ❌");
    }
}
