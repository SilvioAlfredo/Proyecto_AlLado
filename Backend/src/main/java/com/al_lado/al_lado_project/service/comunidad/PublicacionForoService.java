package com.al_lado.al_lado_project.service.comunidad;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PublicacionForoService {
    Page<PublicacionForoDTO> obtenerTodas(Pageable pageable);
    PublicacionForoDTO obtenerPorId(Long id);
    Page<PublicacionForoDTO> obtenerPorAutor(Long autorId, Pageable pageable);

    /**
     * GH-56 (HU-13): publicaciones del foro de un sector.
     *
     * @param sector   sector a filtrar, sin distinguir mayúsculas; vacío o {@code null}
     *                 devuelve todas (mismo orden que {@link #obtenerTodas(Pageable)})
     * @param pageable página y orden
     * @return publicaciones de ese sector; vacío si no hay coincidencias
     */
    Page<PublicacionForoDTO> obtenerPorSector(String sector, Pageable pageable);
    PublicacionForoDTO crear(PublicacionForoDTO dto);
    void eliminar(Long id);
}
