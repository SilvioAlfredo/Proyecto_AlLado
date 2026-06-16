package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.PostulacionDTO;
import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.Postulacion;
import com.al_lado.al_lado_project.entity.Trabajador;
import com.al_lado.al_lado_project.mappers.PostulacionMapper;
import com.al_lado.al_lado_project.repository.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.PostulacionRepository;
import com.al_lado.al_lado_project.repository.TrabajadorRepository;
import com.al_lado.al_lado_project.service.PostulacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostulacionServiceImpl implements PostulacionService {

    private final PostulacionRepository repository;
    private final TrabajadorRepository trabajadorRepository;
    private final OportunidadLaboralRepository oportunidadRepository;
    private final PostulacionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostulacionDTO obtenerPorId(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));
        return mapper.toDTO(postulacion);
    }

    @Override
    @Transactional
    public PostulacionDTO crearPostulacion(PostulacionDTO dto) {
        if (dto.getAutor() == null || dto.getAutor().getId() == null) {
            throw new RuntimeException("El autor (Trabajador) es obligatorio.");
        }
        if (dto.getOportunidad() == null || dto.getOportunidad().getId() == null) {
            throw new RuntimeException("La oportunidad laboral es obligatoria.");
        }

        Long trabajadorId = dto.getAutor().getId();
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado con ID: " + trabajadorId));

        Long oportunidadId = dto.getOportunidad().getId();
        OportunidadLaboral oportunidad = oportunidadRepository.findById(oportunidadId)
                .orElseThrow(() -> new RuntimeException("Oportunidad laboral no encontrada con ID: " + oportunidadId));

        if ("CERRADA".equalsIgnoreCase(oportunidad.getEstado())) {
            throw new RuntimeException("No se permiten postulaciones a una oportunidad cerrada.");
        }

        Postulacion postulacion = mapper.toEntity(dto);
        postulacion.setAutor(trabajador);
        postulacion.setOportunidad(oportunidad);
        postulacion.setEstado("PENDIENTE");

        return mapper.toDTO(repository.save(postulacion));
    }

    @Override
    @Transactional
    public PostulacionDTO seleccionarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        postulacion.setEstado("SELECCIONADA");
        return mapper.toDTO(repository.save(postulacion));
    }

    @Override
    @Transactional
    public PostulacionDTO rechazarPostulacion(Long id) {
        Postulacion postulacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada con ID: " + id));

        postulacion.setEstado("RECHAZADA");
        return mapper.toDTO(repository.save(postulacion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Postulación no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}