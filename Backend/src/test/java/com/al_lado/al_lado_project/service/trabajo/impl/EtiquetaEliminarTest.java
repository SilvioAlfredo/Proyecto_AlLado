package com.al_lado.al_lado_project.service.trabajo.impl;
import com.al_lado.al_lado_project.entity.trabajo.*;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.repository.trabajo.EtiquetaRepository;
import com.al_lado.al_lado_project.mappers.trabajo.EtiquetaMapper;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class EtiquetaEliminarTest {
 @Test void conservaEtiquetasDeTrabajadoresYOportunidades() {
  var repo = mock(EtiquetaRepository.class); var mapper = mock(EtiquetaMapper.class); var etiqueta = new Etiqueta("Plomería"); etiqueta.setId(2L);
  when(repo.findById(2L)).thenReturn(Optional.of(etiqueta)); var service = new EtiquetaServiceImpl(repo,mapper);
  etiqueta.getTrabajadores().add(new Trabajador()); assertThrows(BusinessRuleException.class,()->service.eliminar(2L));
  etiqueta.getTrabajadores().clear(); etiqueta.getOportunidades().add(new OportunidadLaboral()); assertThrows(BusinessRuleException.class,()->service.eliminar(2L));
  verify(repo,never()).delete(any());
 }
 @Test void eliminaSoloEtiquetasLibres() { var repo = mock(EtiquetaRepository.class); var etiqueta = new Etiqueta("Libre"); when(repo.findById(4L)).thenReturn(Optional.of(etiqueta)); new EtiquetaServiceImpl(repo,mock(EtiquetaMapper.class)).eliminar(4L); verify(repo).delete(etiqueta); }
}
