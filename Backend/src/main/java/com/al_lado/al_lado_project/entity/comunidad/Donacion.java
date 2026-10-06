package com.al_lado.al_lado_project.entity.comunidad;

import com.al_lado.al_lado_project.entity.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "donaciones")
@Getter
@Setter
@NoArgsConstructor
public class Donacion {

    public static final String MONEDA_POR_DEFECTO = "ARS";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(length = 10, nullable = false)
    private String moneda = MONEDA_POR_DEFECTO; // Ej: "ARS", "USD"

    @Column(name = "mecanismo_pago", length = 50)
    private String mecanismoPago; // Ej: "MERCADO_PAGO", "TRANSFERENCIA", "STRIPE"

    @Column(name = "fecha_donacion", nullable = false, updatable = false)
    private LocalDateTime fechaDonacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"donaciones", "publicacionesForo", "reportesEnviados", "reportesRecibidos", "suscripcion", "password"})
    private Usuario usuario;

    @Column(length = 20, nullable = false)
    private String estado = "PENDIENTE"; // PENDIENTE, VERIFICADO, RECHAZADO

    @Column(length = 50)
    private String banco; // Banco donde se realizó la transferencia

    @Column(length = 50)
    private String tipoTransferencia; // Ej: "SINPE", "TED", "PIX", "OTRO"

    @Column(length = 100)
    private String asuntoTransferencia; // Asunto que el admin debe verificar

    @PrePersist
    protected void onCreate() {
        if (this.fechaDonacion == null) {
            this.fechaDonacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
    }

    public Donacion(BigDecimal monto, String moneda, String mecanismoPago, Usuario usuario) {
        this.monto = monto;
        this.moneda = moneda != null ? moneda : MONEDA_POR_DEFECTO;
        this.mecanismoPago = mecanismoPago;
        this.usuario = usuario;
        this.estado = "PENDIENTE";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donacion donacion = (Donacion) o;
        return id != null && Objects.equals(id, donacion.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}