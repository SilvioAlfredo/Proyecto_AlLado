package com.al_lado.al_lado_project.entity.usuario;

import com.al_lado.al_lado_project.entity.comunidad.Favorito;
import com.al_lado.al_lado_project.entity.comunidad.PerfilEmpresarial;
import com.al_lado.al_lado_project.entity.comunidad.Publicidad;
import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "empleadores")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@NoArgsConstructor
public class Empleador extends Usuario {

    // Se removió cascade = ALL para evitar borrados en cascada de publicaciones históricas
    @OneToMany(mappedBy = "autor")
    private Set<OportunidadLaboral> oportunidades = new HashSet<>();

    @OneToMany(mappedBy = "autor")
    private Set<Valoracion> valoracionesOtorgadas = new HashSet<>();

    @OneToMany(mappedBy = "empleador", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorito> favoritos = new HashSet<>();

    @OneToOne(mappedBy = "empleador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PerfilEmpresarial perfilEmpresarial;

    @OneToMany(mappedBy = "anunciante")
    private Set<Publicidad> publicidades = new HashSet<>();

    public Empleador(String nombre, String email, String password) {
        super(nombre, email, password);
    }

    public void setPerfilEmpresarial(PerfilEmpresarial perfilEmpresarial) {
        if (perfilEmpresarial == null) {
            if (this.perfilEmpresarial != null) {
                this.perfilEmpresarial.setEmpleador(null);
            }
        } else {
            perfilEmpresarial.setEmpleador(this);
        }
        this.perfilEmpresarial = perfilEmpresarial;
    }
}