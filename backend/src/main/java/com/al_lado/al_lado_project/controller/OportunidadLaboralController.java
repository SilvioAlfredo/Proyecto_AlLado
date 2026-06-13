package com.al_lado.al_lado_project.controller;

import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import com.al_lado.al_lado_project.service.OportunidadLaboralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/oportunidades")
public class OportunidadLaboralController {

    @Autowired
    private OportunidadLaboralService service;

    @GetMapping
    public ResponseEntity<List<OportunidadLaboral>> obtenerTodas() {
        List<OportunidadLaboral> lista = service.obtenerTodas();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OportunidadLaboral> obtenerPorId(@PathVariable Long id) {
        OportunidadLaboral oportunidad = service.obtenerPorId(id);
        return new ResponseEntity<>(oportunidad, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OportunidadLaboral> guardar(@RequestBody OportunidadLaboral oportunidad) {
        OportunidadLaboral nuevaOportunidad = service.guardar(oportunidad);
        return new ResponseEntity<>(nuevaOportunidad, HttpStatus.CREATED);
    }

    @PutMapping("/cerrar/{id}")
    public ResponseEntity<OportunidadLaboral> cerrarOportunidad(@PathVariable Long id) {
        OportunidadLaboral oportunidadCerrada = service.cerrarOportunidad(id);
        return new ResponseEntity<>(oportunidadCerrada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
