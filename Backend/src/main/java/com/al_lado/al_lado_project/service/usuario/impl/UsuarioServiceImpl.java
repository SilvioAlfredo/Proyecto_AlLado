package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;
import com.al_lado.al_lado_project.dto.usuario.EmpleadorDTO;
import com.al_lado.al_lado_project.dto.usuario.UsuarioDTO;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.TrabajadorMapper;
import com.al_lado.al_lado_project.mappers.usuario.EmpleadorMapper;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.EmpleadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final EmpleadorRepository empleadorRepository;
    private final TrabajadorMapper trabajadorMapper;
    private final EmpleadorMapper empleadorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos() {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        
        usuarios.addAll(trabajadorRepository.findAll().stream()
                .map(trabajadorMapper::toDTO)
                .collect(Collectors.toList()));
        
        usuarios.addAll(empleadorRepository.findAll().stream()
                .map(empleadorMapper::toDTO)
                .collect(Collectors.toList()));
        
        return usuarios;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        // Buscar primero en Trabajador
        // Type witness explícito: el Optional se abre como <UsuarioDTO> porque el
        // fallback devuelve un EmpleadorDTO y Optional<TrabajadorDTO> no lo aceptaría.
        return trabajadorRepository.findById(id)
                .<UsuarioDTO>map(trabajadorMapper::toDTO)
                .orElseGet(() -> empleadorRepository.findById(id)
                        .map(empleadorMapper::toDTO)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id)));
    }

    @Override
    @Transactional
    public UsuarioDTO guardar(UsuarioDTO dto) {
        if (dto.getId() != null) {
            SecurityUtils.requireSelfOrAdmin(dto.getId());
        }
        // Determinar el tipo de usuario y delegar al servicio correspondiente
        if (dto instanceof TrabajadorDTO) {
            return trabajadorMapper.toDTO(trabajadorRepository.save(trabajadorMapper.toEntity((TrabajadorDTO) dto)));
        } else if (dto instanceof EmpleadorDTO) {
            return empleadorMapper.toDTO(empleadorRepository.save(empleadorMapper.toEntity((EmpleadorDTO) dto)));
        }
        throw new IllegalArgumentException("Tipo de usuario no soportado");
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        SecurityUtils.requireSelfOrAdmin(id);
        if (trabajadorRepository.existsById(id)) {
            trabajadorRepository.deleteById(id);
        } else if (empleadorRepository.existsById(id)) {
            empleadorRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
    }

    @Override
    @Transactional
    public UsuarioDTO deshabilitar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setHabilitado(false);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioDTO habilitar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setHabilitado(true);
        return toDTO(usuarioRepository.save(usuario));
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        if (usuario instanceof Trabajador trabajador) {
            return trabajadorMapper.toDTO(trabajador);
        }
        if (usuario instanceof Empleador empleador) {
            return empleadorMapper.toDTO(empleador);
        }
        throw new IllegalArgumentException("Tipo de usuario no soportado");
    }
}