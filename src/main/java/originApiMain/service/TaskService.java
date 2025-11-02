package originApiMain.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import originApiMain.exception.TaskNotFoundException;
import originApiMain.model.Task;
import java.util.ArrayList;
import java.util.List;

//Clase para gestionar tareas en memoria:
//Crear nuevas tareas
//Listar todas las tareas
//Actualizar tareas
//Eliminar tareas

@Service
public class TaskService {

    //Utilizaremos memoria local por eso definimos:
    //Lista para almacenar las tareas
    private List<Task> tasks = new ArrayList<>();
    //Contador para asignar ID a cada tarea
    private Integer nextId = 1;

    //Metodo para creacion de tarea
    public Task create(String title, String description){
        Task task = new Task(nextId,title,description,false); //Iniciamos la tarea incompleta
        tasks.add(task); //Añadir la tarea creada a la lista
        nextId ++; //Asegurar que cada ID sea unico incrementando el "contador"
        return task;
    }

    //Metodo para listar todas las tareas almacenadas
    public List<Task> getAll(){
        return tasks;
    }

    //Metodo para listar la tarea especifica por ID
    public Task getById(@PathVariable Integer id){
        for (Task task : tasks){
            if(task.getId() == id){
                return task;
            }
        }
        throw new TaskNotFoundException(id);
    }

    //Metodo para actualizar una tarea
    public Task update(Integer id, String title, String description, boolean completed){
        for (Task task : tasks){
            if(task.getId() == id){
                task.setTitle(title);
                task.setDescription(description);
                task.setCompleted(completed);
                return task;
            }
        }
        throw new TaskNotFoundException(id); //Si no se encuentra la tarea a actualizar lanzamos la excepción
    }

    //Metodo para eliminar una tarea
    public void delete(Integer id){
        for (Task task : tasks){
            if (task.getId() == id){
                tasks.remove(task);
                return;
            }
        }
        throw new TaskNotFoundException(id); //Si no existe el id lanzamos la excepción
    }

}//class
