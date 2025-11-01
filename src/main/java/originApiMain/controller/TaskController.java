package originApiMain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import originApiMain.model.Task;
import originApiMain.service.TaskService;

import java.util.List;


//Clase para controlar las tareas. Endpoints


@RestController
@RequestMapping("/tasks")  // todas las rutas comienzan con /tasks

public class TaskController {
    private final TaskService service;

    //Metodo para inyectar TaskService (para usar sus métodos)
    @Autowired
    public TaskController(TaskService service) {
        this.service = service;
    }

    //Metodo para Endpoint GET /tasks = devolver lista de tareas
    @GetMapping
    public List<Task> getAll() {
        return service.getAll();
    }

    //Metodo para Enpoint Get /tasks = devolver una tarea especifica por ID
    @GetMapping("/{id}")
    public Task getById(int id){
        return service.getById(id);
    }

    //Metodo para Endpoint POST /tasks = crear nueva tarea
    @PostMapping
    public Task create(@RequestBody Task task) {
        return service.create(task.getTitle(), task.getDescription());
    }

    //Metodo para Endpoint PUT /tasks/{id} = actualizar tarea
    @PutMapping("/{id}")
    public Task update(@PathVariable int id, @RequestBody Task task) {
        return service.update(id, task.getTitle(), task.getDescription(), task.isCompleted());
    }

    //Metodo para Endpoint DELETE /tasks/{id} = eliminar tarea
    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        service.delete(id);
        return "☑️ Tarea eliminada con exito";
    }

}//class
