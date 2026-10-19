package com.al_lado.al_lado_project.web;
import com.al_lado.al_lado_project.controller.admin.AdminGestionController;
import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.security.config.SecurityConfig;
import com.al_lado.al_lado_project.security.jwt.*;
import com.al_lado.al_lado_project.security.service.UserDetailsServiceImpl;
import com.al_lado.al_lado_project.repository.seguridad.TokenRevocadoRepository;
import com.al_lado.al_lado_project.service.admin.AdminGestionService;
import com.al_lado.al_lado_project.service.trabajo.OportunidadLaboralService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(AdminGestionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AdminGestionControllerTest {
    @Autowired MockMvc mvc;
    @MockBean AdminGestionService gestion;
    @MockBean OportunidadLaboralService oportunidades;
    @MockBean JwtUtils jwtUtils;
    @MockBean UserDetailsServiceImpl userDetailsService;
    @MockBean TokenRevocadoRepository tokenRevocadoRepository;
    @Test void anonimoDenegado() throws Exception {
        for (var path : List.of("/api/admin/comunicados", "/api/admin/oportunidades", "/api/admin/oportunidades/7/postulaciones", "/api/admin/reportes", "/api/admin/foro", "/api/admin/valoraciones?trabajadorId=3", "/api/admin/donaciones", "/api/admin/suscripciones", "/api/admin/publicidades", "/api/admin/empresas")) mvc.perform(get(path)).andExpect(status().isUnauthorized());
        verifyNoInteractions(gestion, oportunidades);
    }
    @Test @WithMockUser(roles="TRABAJADOR") void trabajadorDenegado() throws Exception {
        for (var path : List.of("/api/admin/comunicados", "/api/admin/oportunidades", "/api/admin/oportunidades/7/postulaciones", "/api/admin/reportes", "/api/admin/foro", "/api/admin/valoraciones?trabajadorId=3", "/api/admin/donaciones", "/api/admin/suscripciones", "/api/admin/publicidades", "/api/admin/empresas")) mvc.perform(get(path)).andExpect(status().isForbidden());
        verifyNoInteractions(gestion, oportunidades);
    }
    @Test @WithMockUser(roles="EMPLEADOR") void empleadorDenegado() throws Exception {
        mvc.perform(get("/api/admin/oportunidades")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(roles="ADMIN") void paginacionYFiltros() throws Exception {
        var dto = new ComunicadoDTO(); dto.setTitulo("Aviso");
        when(gestion.comunicados(eq("seguridad"), eq("Comunidad"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(1,10), 21));
        mvc.perform(get("/api/admin/comunicados").param("filtro","seguridad").param("alcance","Comunidad").param("orden","antiguos").param("page","1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].titulo").value("Aviso")).andExpect(jsonPath("$.totalPages").value(3)).andExpect(jsonPath("$.totalElements").value(21));
        verify(gestion).comunicados(eq("seguridad"), eq("Comunidad"), argThat(p -> p.getPageNumber()==1 && p.getSort().getOrderFor("fechaEmision").isAscending()));
    }
    @Test @WithMockUser(roles="ADMIN") void combinaFiltrosYPaginacion() throws Exception {
        when(oportunidades.listarOportunidades(eq("cañería"), eq("PRESENCIAL"), eq(List.of("Plomería","Electricidad")), any(Pageable.class))).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/oportunidades").param("filtro","cañería").param("modalidad","PRESENCIAL").param("etiquetas","Plomería","Electricidad")).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
    }
    @Test @WithMockUser(roles="ADMIN") void postulacionesPorOferta() throws Exception {
        when(gestion.postulaciones(eq(7L), any(Pageable.class))).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/oportunidades/7/postulaciones", "/api/admin/reportes", "/api/admin/foro", "/api/admin/valoraciones?trabajadorId=3", "/api/admin/donaciones", "/api/admin/suscripciones", "/api/admin/publicidades", "/api/admin/empresas")).andExpect(status().isOk());
        verify(gestion).postulaciones(eq(7L), any(Pageable.class));
    }
    @Test @WithMockUser(roles="ADMIN") void parametrosInvalidos() throws Exception {
        mvc.perform(get("/api/admin/comunicados").param("page","-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/oportunidades").param("size","101")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/oportunidades/7/postulaciones").param("size","0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/comunicados").param("orden","password")).andExpect(status().isBadRequest());
        verifyNoInteractions(gestion, oportunidades);
    }

    @Test @WithMockUser(roles="ADMIN") void moderacionPaginada() throws Exception {
        when(gestion.reportes(eq("fraude"), eq("PENDIENTE"), any(Pageable.class))).thenReturn(Page.empty());
        when(gestion.foro(eq("seguridad"), any(Pageable.class))).thenReturn(Page.empty());
        when(gestion.valoraciones(eq(3L), any(Pageable.class))).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/reportes").param("filtro","fraude")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/admin/foro").param("filtro","seguridad")).andExpect(status().isOk());
        mvc.perform(get("/api/admin/valoraciones").param("trabajadorId","3")).andExpect(status().isOk());
        verify(gestion).valoraciones(eq(3L), any(Pageable.class));
    }
    @Test @WithMockUser(roles="ADMIN") void rechazaFiltrosModeracionInvalidos() throws Exception {
        mvc.perform(get("/api/admin/reportes").param("estado","ABIERTO")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/foro").param("page","-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/valoraciones").param("trabajadorId","0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/valoraciones")).andExpect(status().isBadRequest());
        verifyNoInteractions(gestion);
    }

    @Test @WithMockUser(roles="ADMIN") void aportesPaginadosConFiltros() throws Exception {
        when(gestion.donaciones(eq("Ana"), eq("PENDIENTE"), any(Pageable.class))).thenReturn(Page.empty());
        when(gestion.suscripciones(eq("Ana"), eq("ACTIVAS"), any(Pageable.class))).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/donaciones").param("filtro","Ana").param("estado","PENDIENTE")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/admin/suscripciones").param("filtro","Ana").param("estado","ACTIVAS")).andExpect(status().isOk());
    }
    @Test @WithMockUser(roles="ADMIN") void filtrosAportesInvalidos() throws Exception {
        mvc.perform(get("/api/admin/donaciones").param("estado","ACTIVAS")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/suscripciones").param("estado","PENDIENTE")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/suscripciones").param("size","101")).andExpect(status().isBadRequest());
        verifyNoInteractions(gestion);
    }
    @Test @WithMockUser(roles="ADMIN") void gestionFinalPaginada() throws Exception {
        when(gestion.publicidades(eq("reformas"), eq("VIGENTES"), any(Pageable.class))).thenReturn(Page.empty());
        when(gestion.empresas(eq("sur"), eq("PENDIENTES"), any(Pageable.class))).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/publicidades").param("filtro","reformas").param("estado","VIGENTES")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/admin/empresas").param("filtro","sur").param("estado","PENDIENTES")).andExpect(status().isOk());
    }
    @Test @WithMockUser(roles="ADMIN") void rechazaFiltrosGestionFinal() throws Exception {
        mvc.perform(get("/api/admin/publicidades").param("estado","VERIFICADAS")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/empresas").param("estado","ACTIVAS")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/empresas").param("size","101")).andExpect(status().isBadRequest());
        verifyNoInteractions(gestion);
    }
    @Test @WithMockUser(roles="ADMIN") void incluyeEmpleadoresSinPerfil() throws Exception {
        var owner = new com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO(); owner.setId(8L); owner.setNombre("Camila");
        var item = new com.al_lado.al_lado_project.dto.admin.EmpresaAdminDTO(8L, owner, null);
        when(gestion.empresas(eq("Camila"), eq("SIN_PERFIL"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(item)));
        mvc.perform(get("/api/admin/empresas").param("filtro","Camila").param("estado","SIN_PERFIL"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].empleador.nombre").value("Camila"))
            .andExpect(jsonPath("$.content[0].perfil").doesNotExist());
    }
}
