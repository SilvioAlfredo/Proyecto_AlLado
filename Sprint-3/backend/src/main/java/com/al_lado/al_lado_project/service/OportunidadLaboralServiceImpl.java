package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import com.al_lado.al_lado_project.repository.OportunidadLaboralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OportunidadLaboralServiceImpl implements OportunidadLaboralService{

    private final OportunidadLaboralRepository repository;

    @Override
    public List<OportunidadLaboral> obtenerTodas() {
        return repository.findAll();
    }

    @Override
    public OportunidadLaboral obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Oportunidad no encontrada"));
    }

    @Override
    public OportunidadLaboral guardar(OportunidadLaboral oportunidad) {

        oportunidad.setFechaPublicacion(new Date());

        if (oportunidad.getEstado() == null) {
            oportunidad.setEstado("ACTIVA");
        }

        return repository.save(oportunidad);
    }

    @Override
    public OportunidadLaboral cerrarOportunidad(Long id) {

        OportunidadLaboral oportunidad = obtenerPorId(id);

        oportunidad.setEstado("CERRADA");
        oportunidad.setFechaCierre(new Date());

        return repository.save(oportunidad);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}