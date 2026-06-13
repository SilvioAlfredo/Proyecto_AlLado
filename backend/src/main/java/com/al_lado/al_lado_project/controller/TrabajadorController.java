package com.al_lado.al_lado_project.controller;

import com.al_lado.al_lado_project.entity.Trabajador;
import com.al_lado.al_lado_project.service.TrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trabajadores")
@RequiredArgsConstructor
public class TrabajadorController {

    private final TrabajadorService service;

    @GetMapping
    public List<Trabajador> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Trabajador obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Trabajador guardar(@RequestBody Trabajador trabajador) {
        return service.guardar(trabajador);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
