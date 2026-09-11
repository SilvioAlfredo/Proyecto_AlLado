package com.al_lado.al_lado_project.entity.comunidad;

import com.al_lado.al_lado_project.entity.usuario.Administrador;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "perfiles_empresariales")
@Getter
@Setter
@NoArgsConstructor
public class PerfilEmpresarial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_empresa", nullable = false, length = 100)
    private String nombreEmpresa;

    @Column(length = 20)
    private String cuit;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 150)
    private String sitioWeb;

    @Column(name = "url_logo", length = 255)
    private String urlLogo;

    @Column(nullable = false)
    private boolean verificado = false;

    // Empleador titular de la empresa
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleador_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"perfilEmpresarial", "oportunidades", "favoritos", "valoracionesOtorgadas", "publicidades", "password"})
    private Empleador empleador;

    // Administrador responsable de auditar o verificar esta entidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisado_por_id")
    @JsonIgnoreProperties({"perfilesSupervisados", "comunicadosEnviados", "reportesModerados", "password"})
    private Administrador supervisadoPor;

    public PerfilEmpresarial(String nombreEmpresa, String cuit, String descripcion, String sitioWeb, String urlLogo, Empleador empleador) {
        this.nombreEmpresa = nombreEmpresa;
        this.cuit = cuit;
        this.descripcion = descripcion;
        this.sitioWeb = sitioWeb;
        this.urlLogo = urlLogo;
        this.empleador = empleador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerfilEmpresarial that = (PerfilEmpresarial) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}