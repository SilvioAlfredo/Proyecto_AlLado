package com.al_lado.al_lado_project.controller;

import com.al_lado.al_lado_project.entity.Postulacion;
import com.al_lado.al_lado_project.service.PostulacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/postulaciones")
public class PostulacionController {

    @Autowired
    private PostulacionService service;

    @GetMapping
    public ResponseEntity<List<Postulacion>> obtenerTodas() {
        List<Postulacion> lista = service.obtenerTodas();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Postulacion> obtenerPorId(@PathVariable Long id) {
        Postulacion postulacion = service.obtenerPorId(id);
        return new ResponseEntity<>(postulacion, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Postulacion> crearPostulacion(@RequestBody Postulacion postulacion) {
        Postulacion nuevaPostulacion = service.crearPostulacion(postulacion);
        return new ResponseEntity<>(nuevaPostulacion, HttpStatus.CREATED);
    }

    @PutMapping("/seleccionar/{id}")
    public ResponseEntity<Postulacion> seleccionarPostulacion(@PathVariable Long id) {
        Postulacion postulacionSeleccionada = service.seleccionarPostulacion(id);
        return new ResponseEntity<>(postulacionSeleccionada, HttpStatus.OK);
    }

    @PutMapping("/rechazar/{id}")
    public ResponseEntity<Postulacion> rechazarPostulacion(@PathVariable Long id) {
        Postulacion postulacionRechazada = service.rechazarPostulacion(id);
        return new ResponseEntity<>(postulacionRechazada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
