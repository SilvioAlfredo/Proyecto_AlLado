package com.al_lado.al_lado_project.service.comunidad.impl;
import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import com.al_lado.al_lado_project.entity.usuario.Trabajador;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import com.al_lado.al_lado_project.exception.BusinessRuleException;
import com.al_lado.al_lado_project.mappers.comunidad.DonacionMapper;
import com.al_lado.al_lado_project.repository.comunidad.DonacionRepository;
import com.al_lado.al_lado_project.repository.usuario.UsuarioRepository;
import com.al_lado.al_lado_project.repository.usuario.SuscripcionRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TransferenciaVerificacionTest {
    @Mock DonacionRepository repository;
    @Mock UsuarioRepository usuarios;
    @Mock SuscripcionRepository subs;
    @Mock DonacionMapper mapper;
    DonacionServiceImpl service;
    Donacion record;
    @BeforeEach void setup() {
        service = new DonacionServiceImpl(repository, usuarios, subs, mapper);
        record = new Donacion(); record.setEstado("PENDIENTE"); record.setAsuntoTransferencia("REF-123"); record.setMonto(new BigDecimal("12000.00")); record.setUsuario(new Trabajador()); record.getUsuario().setId(3L);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(record));
    }
    @Test void datosQueNoCoincidenNoSeProcesan() {
        assertThrows(BusinessRuleException.class, () -> service.verificarTransferencia(1L,"OTRO",new BigDecimal("12000")));
        assertThrows(BusinessRuleException.class, () -> service.verificarTransferencia(1L,"REF-123",new BigDecimal("11999.99")));
        assertEquals("PENDIENTE",record.getEstado()); verify(repository,never()).save(any()); verifyNoInteractions(subs);
    }
    @Test void aceptaMismoMontoConDiferenteEscalaSinActivarProEnDonacion() {
        record.setMecanismoPago("TRANSFERENCIA");
        when(repository.save(record)).thenReturn(record);
        service.verificarTransferencia(1L,"REF-123",new BigDecimal("12000.0"));
        assertEquals("VERIFICADO",record.getEstado()); verifyNoInteractions(subs);
    }
    @Test void suscripcionOtorgaProUnaVez() {
        record.setMecanismoPago("SUSCRIPCION"); when(repository.save(record)).thenReturn(record);
        when(usuarios.findById(3L)).thenReturn(Optional.of(record.getUsuario()));
        service.verificarTransferencia(1L,"REF-123",new BigDecimal("12000"));
        var captor = ArgumentCaptor.forClass(Suscripcion.class); verify(subs).save(captor.capture());
        assertEquals("PRO",captor.getValue().getNivel()); assertTrue(captor.getValue().isActiva());
        assertEquals(LocalDate.now().plusYears(1),captor.getValue().getFechaFin());
        assertThrows(BusinessRuleException.class, () -> service.verificarTransferencia(1L,"REF-123",new BigDecimal("12000")));
        verify(repository,times(1)).save(record); verify(subs,times(1)).save(any());
    }
    @Test void reactivaPlanExistenteEnLugarDeCrearOtro() {
        var existing = new Suscripcion(); existing.setNivel("BASICO"); existing.setActiva(false); record.getUsuario().setSuscripcion(existing);
        record.setMecanismoPago("SUSCRIPCION"); when(repository.save(record)).thenReturn(record);
        when(usuarios.findById(3L)).thenReturn(Optional.of(record.getUsuario()));
        service.verificarTransferencia(1L,"REF-123",new BigDecimal("12000"));
        verify(subs).save(existing); assertEquals("PRO",existing.getNivel()); assertTrue(existing.isActiva());
    }
    @Test void registroProcesadoNoSeVerificaOtraVez() {
        record.setEstado("VERIFICADO");
        assertThrows(BusinessRuleException.class, () -> service.verificarTransferencia(1L,"REF-123",new BigDecimal("12000")));
        verify(repository,never()).save(any()); verifyNoInteractions(subs);
    }
}
