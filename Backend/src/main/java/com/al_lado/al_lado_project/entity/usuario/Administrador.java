package com.al_lado.al_lado_project.entity.usuario;

import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import com.al_lado.al_lado_project.entity.comunidad.Reporte;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "administradores")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {

    @Column(name = "nivel_acceso", nullable = false, length = 50)
    private String nivelAcceso;

    // CORREGIDO: Se cambia mappedBy = "administrador" a mappedBy = "emisor"
    @OneToMany(mappedBy = "emisor")
    private Set<Comunicado> comunicadosEnviados = new HashSet<>();

    @OneToMany(mappedBy = "moderador")
    private Set<Reporte> reportesModerados = new HashSet<>();

    @OneToMany(mappedBy = "supervisadoPor")
    private Set<PerfilEmpresarial> perfilesSupervisados = new HashSet<>();

    public Administrador(String nombre, String email, String password, String nivelAcceso) {
        super(nombre, email, password);
        this.nivelAcceso = nivelAcceso;
    }
}