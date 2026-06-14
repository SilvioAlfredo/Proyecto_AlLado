package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.Empleador;
import com.al_lado.al_lado_project.repository.EmpleadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadorServiceImpl implements EmpleadorService {

    private final EmpleadorRepository repository;

    @Override
    public List<Empleador> obtenerTodos() {
        return repository.findAll();
    }

    @Override
    public Empleador obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado"));
    }

    @Override
    public Empleador guardar(Empleador empleador) {
        return repository.save(empleador);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
