package com.al_lado.al_lado_project.service.comunidad.impl;
import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import com.al_lado.al_lado_project.entity.usuario.*;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import com.al_lado.al_lado_project.repository.comunidad.PerfilEmpresarialRepository;
import com.al_lado.al_lado_project.repository.usuario.*;
import com.al_lado.al_lado_project.service.usuario.SuscripcionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class EmpresaVerificacionTest {
 @Mock PerfilEmpresarialRepository repo; @Mock EmpleadorRepository owners; @Mock AdministradorRepository admins;
 @Mock PerfilEmpresarialMapper mapper; @Mock SuscripcionService subs;
 PerfilEmpresarialServiceImpl service() { return new PerfilEmpresarialServiceImpl(repo, owners, admins, mapper, subs); }
 Administrador login() { var admin = new Administrador(); admin.setId(9L); SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin,null,admin.getAuthorities())); return admin; }
 @AfterEach void clear() { SecurityContextHolder.clearContext(); }
 @Test void usaAdministradorActualYReintentoNoCambiaSupervisor() {
  var admin = login(); var perfil = new PerfilEmpresarial(); perfil.setId(5L);
  when(repo.findById(5L)).thenReturn(Optional.of(perfil)); when(admins.findById(9L)).thenReturn(Optional.of(admin)); when(repo.save(perfil)).thenReturn(perfil);
  service().verificar(5L,null); service().verificar(5L,null);
  assertTrue(perfil.isVerificado()); assertSame(admin,perfil.getSupervisadoPor()); verify(repo,times(1)).save(perfil);
 }
 @Test void impideAtribuirRevisionAOtroAdministrador() { login(); assertThrows(AccessDeniedException.class,()->service().verificar(5L,1L)); verifyNoInteractions(repo,admins); }
 @Test void rechazaTrabajadorSinConsultarPerfil() { var worker = new Trabajador(); worker.setId(3L); SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(worker,null,worker.getAuthorities())); assertThrows(AccessDeniedException.class,()->service().verificar(5L,null)); verifyNoInteractions(repo,admins); }
}
