import { useEffect, useState } from 'react';
import { api } from '../api/client';

interface Oportunidad {
  id: number;
  titulo: string;
  descripcion: string;
  estado: string;
  autor?: { nombre: string };
}

interface FeedPageProps {
  onSeleccionarOferta: (oferta: Oportunidad) => void;
  onIrACrear: () => void;
}

export function FeedPage({ onSeleccionarOferta, onIrACrear }: FeedPageProps) {
  const [oportunidades, setOportunidades] = useState<Oportunidad[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/oportunidades')
      .then((res) => setOportunidades(res.data))
      .catch((err) => console.error('Error al cargar ofertas:', err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="inicio-screen"><p>Cargando publicaciones...</p></div>;

  return (
    <section className="inicio-screen">
      <header className="inicio-header">
        <span>AlLado</span>
        <span className="perfil" aria-label="Perfil de usuario" />
      </header>

      <select className="buscador" defaultValue="">
        <option value="">Buscar por Especialidad</option>
        <option>Electricidad</option>
        <option>Plomería</option>
        <option>Gas</option>
      </select>

      <h1 className="publicaciones-title">Publicaciones</h1>

      <div className="publicaciones-list">
        {oportunidades.map((op) => (
          <button
            key={op.id}
            className="publicacion-card"
            type="button"
            onClick={() => onSeleccionarOferta(op)}
          >
            <span className="publicacion-avatar" />
            <span className="publicacion-info">
              <strong>{op.autor?.nombre || 'Usuario'}</strong>
              <small>{op.titulo}</small>
            </span>
          </button>
        ))}
      </div>

      <button
        className="boton-agregar"
        type="button"
        aria-label="Crear solicitud"
        onClick={onIrACrear}
      >
        +
      </button>

      <footer className="inicio-footer">Inicio</footer>
    </section>
  );
}