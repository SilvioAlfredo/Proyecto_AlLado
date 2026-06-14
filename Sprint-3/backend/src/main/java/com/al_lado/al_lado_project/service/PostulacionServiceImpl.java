package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.Postulacion;
import com.al_lado.al_lado_project.repository.PostulacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostulacionServiceImpl implements PostulacionService {

    private final PostulacionRepository repository;

    @Override
    public List<Postulacion> obtenerTodas() {
        return repository.findAll();
    }

    @Override
    public Postulacion obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Postulación no encontrada"));
    }

    @Override
    public Postulacion crearPostulacion(Postulacion postulacion) {

        postulacion.setEstado("PENDIENTE");

        return repository.save(postulacion);
    }

    @Override
    public Postulacion seleccionarPostulacion(Long id) {

        Postulacion postulacion = obtenerPorId(id);

        postulacion.setEstado("SELECCIONADA");

        return repository.save(postulacion);
    }

    @Override
    public Postulacion rechazarPostulacion(Long id) {

        Postulacion postulacion = obtenerPorId(id);

        postulacion.setEstado("RECHAZADA");

        return repository.save(postulacion);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}