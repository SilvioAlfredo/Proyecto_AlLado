package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.Trabajador;
import com.al_lado.al_lado_project.repository.TrabajadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository repository;

    @Override
    public List<Trabajador> obtenerTodos() {
        return repository.findAll();
    }

    @Override
    public Trabajador obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Trabajador no encontrado"));
    }

    @Override
    public Trabajador guardar(Trabajador trabajador) {
        return repository.save(trabajador);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}