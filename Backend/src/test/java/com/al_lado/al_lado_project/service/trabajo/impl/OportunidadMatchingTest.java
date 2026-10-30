package com.al_lado.al_lado_project.service.trabajo.impl;

import com.al_lado.al_lado_project.dto.trabajo.CandidatoMatchingDTO;
import com.al_lado.al_lado_project.entity.perfil.HorarioDisponibilidad;
import com.al_lado.al_lado_project.entity.trabajo.Etiqueta;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.repository.trabajo.OportunidadLaboralRepository;
import com.al_lado.al_lado_project.repository.usuario.TrabajadorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Proxy;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OportunidadMatchingTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void filtraPorRadioYOrdenaPorHabilidadDistanciaYDisponibilidad() {
        Empleador empleador = new Empleador();
        empleador.setId(10L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(empleador, null, empleador.getAuthorities()));

        OportunidadLaboral oportunidad = new OportunidadLaboral();
        oportunidad.setId(5L);
        oportunidad.setAutor(empleador);
        oportunidad.setActiva(true);
        oportunidad.setModalidad("PRESENCIAL");
        oportunidad.setLatitud(-34.60);
        oportunidad.setLongitud(-58.49);
        oportunidad.setDiaSemanaRequerido("LUNES");
        oportunidad.setHoraInicioRequerida(LocalTime.of(10, 0));
        oportunidad.setHoraFinRequerida(LocalTime.of(11, 0));
        oportunidad.setEtiquetas(Set.of(new Etiqueta("Plomería")));
        Trabajador mejor = trabajador(1L, "Ana", "Villa del Parque", "Plomeria",
                -34.60, -58.49, "LUNES");
        Trabajador cercano = trabajador(2L, "Beto", "Otro barrio", "PLOMERÍA",
                -34.61, -58.49, "LUNES");
        Trabajador horarioIncompatible = trabajador(3L, "Ceci", "Cerca", "Plomería",
                -34.605, -58.49, "MARTES");
        Trabajador fueraDelRadio = trabajador(4L, "Dani", "Lejos", "Plomería",
                -34.90, -58.49, "LUNES");
        List<Trabajador> trabajadores = List.of(fueraDelRadio, horarioIncompatible, cercano, mejor);
        OportunidadLaboralRepository repository = proxy(OportunidadLaboralRepository.class,
                (method, args) -> "findById".equals(method) ? Optional.of(oportunidad) : null);
        TrabajadorRepository trabajadorRepository = proxy(TrabajadorRepository.class,
                (method, args) -> "findAllHabilitadosParaMatching".equals(method) ? trabajadores : null);

        OportunidadLaboralServiceImpl service = new OportunidadLaboralServiceImpl(
                repository, null, null, null, trabajadorRepository, null);

        List<CandidatoMatchingDTO> resultado = service.candidatosParaOportunidad(5L, 10);

        assertEquals(List.of(1L, 2L), resultado.stream()
                .map(CandidatoMatchingDTO::getTrabajadorId).toList());
        assertEquals(100, resultado.get(0).getPuntuacion());
        assertEquals(List.of("Plomeria"), resultado.get(0).getEspecialidadesCoincidentes());
        assertEquals(0.0, resultado.get(0).getDistanciaKm());
        assertEquals(true, resultado.get(0).isDisponibilidadCoincidente());
        assertEquals(true, resultado.get(1).isDisponibilidadCoincidente());
        assertEquals(true, resultado.stream().allMatch(CandidatoMatchingDTO::isDentroDelRadio));
    }

    private static Trabajador trabajador(Long id, String nombre, String sector,
                                          String especialidad, double latitud, double longitud,
                                          String diaDisponible) {
        Trabajador trabajador = new Trabajador();
        trabajador.setId(id);
        trabajador.setNombre(nombre);
        trabajador.setSector(sector);
        trabajador.setEspecialidades(especialidad == null ? Set.of()
                : Set.of(new Etiqueta(especialidad)));
        trabajador.setLatitud(latitud);
        trabajador.setLongitud(longitud);
        trabajador.setHorarios(Set.of(new HorarioDisponibilidad(diaDisponible,
                LocalTime.of(9, 0), LocalTime.of(12, 0), trabajador)));
        return trabajador;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> tipo, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo},
                (proxy, method, args) -> handler.invoke(method.getName(), args));
    }

    @FunctionalInterface
    private interface InvocationHandler {
        Object invoke(String method, Object[] args);
    }
}
