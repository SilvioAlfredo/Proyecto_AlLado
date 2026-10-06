import { useEffect, useState } from 'react';
import { api } from '../api/client';

interface Etiqueta {
  nombre: string;
}

interface Horario {
  dia: string;
  horaInicio: string;
  horaFin: string;
}

interface PortafolioItem {
  id: number;
  titulo: string;
  descripcion: string;
  urlImagen: string;
}

interface PreferenciaNotificacion {
  notificarEmail: boolean;
  notificarPush: boolean;
  alertasNuevasOfertas: boolean;
}

interface PerfilTrabajador {
  id: number;
  nombre: string;
  email: string;
  telefono: string;
  fotografia?: string;
  biografia?: string;
  certificados?: string;
  especialidades: string[];
  horarios: Horario[];
  portafolioItems: PortafolioItem[];
  preferenciaNotificacion: PreferenciaNotificacion;
  nivelRecomendacion?: number;
  valoracionesCount?: number;
}

interface PerfilTrabajadorProps {
  trabajadorId: number;
  onVolver?: () => void;
}

export function PerfilTrabajadorPage({ trabajadorId, onVolver }: PerfilTrabajadorProps) {
  const [trabajador, setTrabajador] = useState<PerfilTrabajador | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get(`/trabajadores/${trabajadorId}`)
      .then((res) => {
        setTrabajador(res.data);
      })
      .catch((err) => console.error('Error al cargar perfil de trabajador:', err))
      .finally(() => setLoading(false));
  }, [trabajadorId]);

  if (loading) return <div className="screen"><p>Cargando perfil...</p></div>;

  if (!trabajador) return <div className="screen"><p>Perfil no disponible</p></div>;

  return (
    <div className="screen">
      <header className="screen-header">
        <h1 className="screen-title">{trabajador.nombre}</h1>
      </header>

      <div className="perfil-container">
        {/* Foto de perfil */}
        <div className="fotografia-box">
          {trabajador.fotografia 
            ? <img src={trabajador.fotografia} alt="Foto de perfil" className="fotografia-img" />
            : <div className="fotografia-placeholder">Foto</div>
          }
        </div>

        {/* Datos principales */}
        <div className="datos-personales">
          <p className="telefono">{trabajador.telefono}</p>
          <p className="biografia">{trabajador.biografia || 'Sin biografía'}</p>
          {trabajador.certificados && <p className="certificados">Certificados: {trabajador.certificados}</p>}
        </div>

        {/* Especialidades */}
        <div className="especialidades">
          <h3>Especialidades</h3>
          <div className="tags">
            {trabajador.especialidades.map((etq: string) => (
              <span key={etq} className="tag">{etq}</span>
            ))}
          </div>
        </div>

        {/* Nivel de recomendación */}
        {trabajador.nivelRecomendacion !== undefined && (
          <div className="nivel-recomendacion">
            <h3>Nivel de recomendación</h3>
            <p>{trabajador.nivelRecomendacion}/5 estrellas</p>
          </div>
        )}

        {/* Valoraciones */}
        {trabajador.valoracionesCount !== undefined && (
          <div className="valoraciones-count">
            <p>{trabajador.valoracionesCount} valoraciones</p>
          </div>
        )}

        {/* Horarios de disponibilidad */}
        <div className="horarios">
          <h3>Horarios de disponibilidad</h3>
          <div className "horarios-lista">
            {trabajador.horarios.map((h: Horario) => (
              <div key={h.dia} className="horario-item">
                <span>{h.dia}</span>
                <span>{h.horaInicio} - {h.horaFin}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Portafolio */}
        <div className="portafolio">
          <h3>Portafolio</h3>
          <div className="portafolio-grid">
            {trabajador.portafolioItems.map((item: PortafolioItem) => (
              <div key={item.id} className="portafolio-item">
                {item.urlImagen ? (
                  <img src={item.urlImagen} alt={item.titulo} className="portafolio-img" />
                ) : (
                  <div className="portafolio-placeholder">Portafolio</div>
                )}
                <span className="portafolio-titulo">{item.titulo}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <footer className="screen-footer">
        <button
          className="boton-favorito"
          aria-label="Agregar a favoritos"
          onClick={() => {
            // TODO: Implementar agregar a favoritos
            alert('Funcionalidad por implementar: Agregar a favoritos');
          }}
        >
          ⭐ Agregar a favoritos
        </button>
        {onVolver && (
          <button
            className="volver"
            type="button"
            onClick={onVolver}
          >
            <span className="volver-arrow">←</span> Volver
          </button>
        )}
      </footer>
    </div>
  );
}