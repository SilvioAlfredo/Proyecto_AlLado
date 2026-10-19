package com.al_lado.al_lado_project.dto.admin;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.dto.comunidad.PerfilEmpresarialDTO;

/**
 * Ficha de empleador para el listado administrativo de empresas.
 *
 * <p>Separa explícitamente la identidad del empleador de la de su perfil: un
 * empleador puede no haber creado perfil empresarial todavía (el listado los
 * conserva con el filtro {@code SIN_PERFIL}).</p>
 *
 * @param id       ID del <b>empleador</b>, distinto conceptualmente de
 *                 {@code perfil.id}. Identifican recursos independientes aunque
 *                 sus valores numéricos puedan coincidir.
 * @param empleador resumen con ID, nombre y email del empleador.
 * @param perfil   perfil empresarial o <b>{@code null}</b> si ese empleador aún no
 *                 tiene uno cargado.
 */
public record EmpresaAdminDTO(Long id, UsuarioResumenDTO empleador, PerfilEmpresarialDTO perfil) {

}
