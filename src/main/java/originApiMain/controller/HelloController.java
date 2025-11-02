package originApiMain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Esta clase es un RestController de Spring Boot
// Maneja endpoints básicos de prueba para OriginAPI
//Fue creada para verificar la puesta en marcha del proyecto.

@RestController

//Controlador para Endpoints de prueba
public class HelloController {
    @GetMapping("/hello") //Asigna la ruta /hello para futuros Get

    //Metodo que devuelve un mensaje de bienvenida para /hello
    public String hello() {
        return "Hola, Bienvenido";
    }

}//class
