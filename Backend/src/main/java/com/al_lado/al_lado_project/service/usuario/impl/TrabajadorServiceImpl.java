package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.usuario.TrabajadorDTO;
import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.TrabajadorMapper;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.security.SecurityUtils;
import com.al_lado.al_lado_project.service.usuario.TrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final TrabajadorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDTO> obtenerTodos() {
        // AL-SEC-006: cada ficha se proyecta según quién pregunta; el directorio no
        // expone datos de contacto de terceros.
        return repository.findAll().stream()
                .map(this::proyectar)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDTO> filtrar(String etiqueta, String sector) {
        String etiquetaNormalizada = etiqueta == null || etiqueta.isBlank() ? null : etiqueta.trim();
        String sectorNormalizado = sector == null || sector.isBlank() ? null : sector.trim();
        return repository.filtrarPorEtiquetaYSector(etiquetaNormalizada, sectorNormalizado).stream()
                .map(this::proyectar)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajadorDTO obtenerPorId(Long id) {
        Trabajador trabajador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + id));
        return proyectar(trabajador);
    }

    /**
     * Elige la proyección según la audiencia (AL-SEC-006): el titular de la cuenta y
     * ADMIN reciben la ficha completa, el resto la proyección pública.
     */
    private TrabajadorDTO proyectar(Trabajador trabajador) {
        if (trabajador == null) return null;
        return SecurityUtils.canViewPrivateDataOf(trabajador.getId())
                ? mapper.toDTO(trabajador)
                : mapper.toDTOPublic(trabajador);
    }

    @Override
    @Transactional
    public TrabajadorDTO guardar(TrabajadorDTO dto) {
        Trabajador trabajador;

        if (dto.getId() == null) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado.");
            }
            trabajador = mapper.toEntity(dto);
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                trabajador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        } else {
            SecurityUtils.requireSelfOrAdmin(dto.getId());
            trabajador = repository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con ID: " + dto.getId()));

            if (!Objects.equals(trabajador.getEmail(), dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado por otro usuario.");
            }

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                trabajador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        }

        trabajador.setNombre(dto.getNombre());
        trabajador.setEmail(dto.getEmail());
        trabajador.setTelefono(dto.getTelefono());

        // RF-02, HU-03: el update tambien debe persistir el perfil.
        trabajador.setFotografia(dto.getFotografia());
        trabajador.setBiografia(dto.getBiografia());
        trabajador.setCertificados(dto.getCertificados());
        trabajador.setSector(dto.getSector());
        trabajador.setLatitud(dto.getLatitud());
        trabajador.setLongitud(dto.getLongitud());

        // Especialidades siempre como entidades gestionadas (evita
        // duplicados por unique y re-inserciones en lote).
        if (dto.getEspecialidades() != null) {
            Set<Etiqueta> especialidades = dto.getEspecialidades().stream()
                    .map(e -> resolverEtiqueta(e.getId(), e.getNombre()))
                    .collect(Collectors.toSet());
            trabajador.setEspecialidades(especialidades);
        }

        return mapper.toDTO(repository.save(trabajador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        SecurityUtils.requireSelfOrAdmin(id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Trabajador no encontrado con ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new BusinessRuleException("No se puede eliminar el trabajador porque tiene elementos asociados.");
        }
    }

    private Etiqueta resolverEtiqueta(Long id, String nombre) {
        if (id != null) {
            return etiquetaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + id));
        }
        String nombreNormalizado = nombre != null ? nombre.trim() : null;
        if (nombreNormalizado == null || nombreNormalizado.isEmpty()) {
            throw new BusinessRuleException("El nombre de la etiqueta es obligatorio.");
        }
        return etiquetaRepository.findByNombreIgnoreCase(nombreNormalizado)
                .orElseGet(() -> etiquetaRepository.save(new Etiqueta(nombreNormalizado)));
    }
}