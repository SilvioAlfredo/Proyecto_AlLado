package com.al_lado.al_lado_project.service.usuario.impl;

import com.al_lado.al_lado_project.dto.admin.DashboardMetricsDTO;
import com.al_lado.al_lado_project.dto.trabajo.TendenciaDTO;
import com.al_lado.al_lado_project.dto.usuario.AdministradorDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.exception.DuplicateResourceException;
import com.al_lado.al_lado_project.exception.ResourceNotFoundException;
import com.al_lado.al_lado_project.mappers.usuario.AdministradorMapper;
import com.al_lado.al_lado_project.repository.comunidad.DonacionRepository;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.trabajo.PostulacionRepository;
import com.al_lado.al_lado_project.repository.usuario.AdministradorRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.service.usuario.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    // Repositorios para el cálculo de métricas
    private final DonacionRepository donacionRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final PostulacionRepository postulacionRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final OportunidadLaboralRepository oportunidadLaboralRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdministradorDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdministradorDTO obtenerPorId(Long id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));
        return mapper.toDTO(administrador);
    }

    @Override
    @Transactional
    public AdministradorDTO guardar(AdministradorDTO dto) {
        Administrador administrador;

        if (dto.getId() == null) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado.");
            }
            administrador = mapper.toEntity(dto);
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                administrador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        } else {
            administrador = repository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + dto.getId()));

            if (!administrador.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("El email ya se encuentra registrado por otro usuario.");
            }

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                administrador.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        }

        administrador.setNombre(dto.getNombre());
        administrador.setEmail(dto.getEmail());
        administrador.setNivelAcceso(dto.getNivelAcceso());

        return mapper.toDTO(repository.save(administrador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Administrador no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsDTO obtenerMetricasDashboard() {
        Locale localeAr = new Locale("es", "AR");
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(localeAr);
        formatoMoneda.setMaximumFractionDigits(0);

        // 1. Ingresos por donaciones y suscripciones
        double totalDonaciones = donacionRepository.findAll().stream()
                .mapToDouble(d -> d.getMonto() != null ? d.getMonto().doubleValue() : 0.0)
                .sum();

        final double PRECIO_PLAN_PRO = 15000.0;
        double totalSuscripciones = suscripcionRepository.findAll().stream()
                .filter(s -> s.isActiva())
                .mapToDouble(s -> "PRO".equalsIgnoreCase(s.getNivel()) ? PRECIO_PLAN_PRO : 0.0)
                .sum();

        double totalIngresos = totalDonaciones + totalSuscripciones;

        // 2. Postulaciones y Trabajadores
        List<Postulacion> listaPostulaciones = postulacionRepository.findAll();
        long totalTrabajadores = trabajadorRepository.count();

        // 3. Remuneración pretendida promedio
        double avgContrato = listaPostulaciones.stream()
                .filter(p -> p.getRemuneracionPretendida() != null && p.getRemuneracionPretendida() > 0)
                .mapToDouble(Postulacion::getRemuneracionPretendida)
                .average()
                .orElse(0.0);

        // 4. Contratos finalizados y Dinero en la comunidad
        List<Postulacion> finalizados = listaPostulaciones.stream()
                .filter(p -> "FINALIZADA".equalsIgnoreCase(p.getEstado()))
                .toList();

        double dineroComunidad = finalizados.stream()
                .filter(p -> p.getRemuneracionPretendida() != null)
                .mapToDouble(Postulacion::getRemuneracionPretendida)
                .sum();

        long trabajosCompletados = finalizados.size();

        // 5. Cálculo de tendencias
        long totalOportunidades = oportunidadLaboralRepository.count();
        List<TendenciaDTO> tendencias = new ArrayList<>();
        List<Object[]> rawTendencias = oportunidadLaboralRepository.findTendenciasPorEtiqueta();

        String[] paletaColores = {"fill-yellow", "fill-yellow", "fill-gold", "fill-dark-gold", "fill-dark-gold"};

        int index = 0;
        for (Object[] row : rawTendencias) {
            if (index >= 5) break;

            String nombre = (String) row[0];
            long count = (Long) row[1];

            int porcentaje = (totalOportunidades > 0)
                    ? (int) Math.round(((double) count / totalOportunidades) * 100)
                    : 0;

            String color = paletaColores[Math.min(index, paletaColores.length - 1)];
            tendencias.add(new TendenciaDTO(nombre, count, porcentaje, color));
            index++;
        }

        return new DashboardMetricsDTO(
                formatoMoneda.format(totalIngresos),
                (long) listaPostulaciones.size(),
                totalTrabajadores,
                formatoMoneda.format(avgContrato),
                formatoMoneda.format(dineroComunidad),
                trabajosCompletados,
                tendencias
        );
    }
}