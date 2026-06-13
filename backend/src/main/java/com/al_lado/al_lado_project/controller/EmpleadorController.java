package com.al_lado.al_lado_project.controller;

import com.al_lado.al_lado_project.entity.Empleador;
import com.al_lado.al_lado_project.service.EmpleadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleadores")
@RequiredArgsConstructor
public class EmpleadorController {

    private final EmpleadorService service;

    @GetMapping
    public List<Empleador> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Empleador obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empleador guardar(@RequestBody Empleador empleador) {
        return service.guardar(empleador);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
