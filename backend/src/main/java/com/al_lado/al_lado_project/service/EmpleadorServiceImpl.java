package com.al_lado.al_lado_project.service;

import com.al_lado.al_lado_project.dto.EmpleadorDTO;
import com.al_lado.al_lado_project.dto.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.entity.Empleador;
import com.al_lado.al_lado_project.repository.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.UsuarioRepository;
import com.al_lado.al_lado_project.service.EmpleadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadorServiceImpl implements EmpleadorService {

    private final EmpleadorRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadorDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadorDTO obtenerPorId(Long id) {
        Empleador empleador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado con ID: " + id));
        return convertToDto(empleador);
    }

    @Override
    @Transactional
    public EmpleadorDTO guardar(EmpleadorDTO dto) {
        Empleador empleador;

        if (dto.getId() == null) {
            // Creación: Validar email único
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado.");
            }
            empleador = new Empleador();
        } else {
            // Edición: Validar que si cambia el email, no pertenezca a otro ID
            empleador = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Empleador no encontrado"));

            if (!empleador.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado por otro usuario.");
            }
        }

        empleador.setNombre(dto.getNombre());
        empleador.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            empleador.setPassword(dto.getPassword());
        }

        return convertToDto(repository.save(empleador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Empleador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    private EmpleadorDTO convertToDto(Empleador entity) {
        EmpleadorDTO dto = new EmpleadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());

        if (entity.getOportunidades() != null) {
            List<OportunidadLaboralResumenDTO> resumenes = entity.getOportunidades().stream().map(op -> {
                OportunidadLaboralResumenDTO resumen = new OportunidadLaboralResumenDTO();
                resumen.setId(op.getId());
                resumen.setTitulo(op.getTitulo());
                resumen.setDescripcion(op.getDescripcion());
                resumen.setImagen(op.getImagen());
                resumen.setEstado(op.getEstado());
                resumen.setFechaPublicacion(op.getFechaPublicacion());
                resumen.setFechaCierre(op.getFechaCierre());
                return resumen;
            }).collect(Collectors.toList());
            dto.setOportunidades(resumenes);
        }
        return dto;
    }
}