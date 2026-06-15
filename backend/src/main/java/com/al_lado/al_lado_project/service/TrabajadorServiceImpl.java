package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.PostulacionResumenDTO;
import com.al_lado.al_lado_project.dto.TrabajadorDTO;
import com.al_lado.al_lado_project.dto.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.Trabajador;
import com.al_lado.al_lado_project.repository.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.UsuarioRepository;
import com.al_lado.al_lado_project.service.TrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajadorDTO obtenerPorId(Long id) {
        Trabajador trabajador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado con ID: " + id));
        return convertToDto(trabajador);
    }

    @Override
    @Transactional
    public TrabajadorDTO guardar(TrabajadorDTO dto) {
        Trabajador trabajador;

        if (dto.getId() == null) {
            // Creación: Validar email único
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado.");
            }
            trabajador = new Trabajador();
        } else {
            // Edición: Validar que si cambia el email, no pertenezca a otro ID
            trabajador = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Empleador no encontrado"));

            if (!trabajador.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado por otro usuario.");
            }
        }

        trabajador.setNombre(dto.getNombre());
        trabajador.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            trabajador.setPassword(dto.getPassword());
        }

        return convertToDto(repository.save(trabajador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Trabajador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    private TrabajadorDTO convertToDto(Trabajador entity) {
        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());

        if (entity.getPostulaciones() != null) {
            List<PostulacionResumenDTO> resumenes = entity.getPostulaciones().stream().map(postulacion -> {
                PostulacionResumenDTO resumen = new PostulacionResumenDTO();
                resumen.setId(postulacion.getId());
                resumen.setPresupuesto(postulacion.getPresupuesto());
                resumen.setDisponibilidad(postulacion.getDisponibilidad());
                resumen.setEstado(postulacion.getEstado());

                if (postulacion.getAutor() != null) {
                    UsuarioResumenDTO autorResumen = new UsuarioResumenDTO();
                    autorResumen.setId(postulacion.getAutor().getId());
                    autorResumen.setNombre(postulacion.getAutor().getNombre());
                    autorResumen.setEmail(postulacion.getAutor().getEmail());
                    resumen.setAutor(autorResumen);
                }
                return resumen;
            }).collect(Collectors.toList());
            dto.setPostulaciones(resumenes);
        }
        return dto;
    }
}