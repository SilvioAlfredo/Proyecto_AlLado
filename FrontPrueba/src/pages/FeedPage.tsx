import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { FavoriteButton } from '../components/FavoriteButton';

interface Oportunidad {
  id: number;
  titulo: string;
  descripcion: string;
  ubicacion: string;
  modalidad: string;
  rangoSalarial: string;
  estado: string;
  autor?: { id: number; nombre: string };
  etiquetas?: string[];
}

interface FeedPageProps {
  onSeleccionarOferta: (oferta: Oportunidad) => void;
  onIrACrear: () => void;
}

export function FeedPage({ onSeleccionarOferta, onIrACrear }: FeedPageProps) {
  const [oportunidades, setOportunidades] = useState<Oportunidad[]>([]);
  const [loading, setLoading] = useState(true);
  const [filtroEspecialidad, setFiltroEspecialidad] = useState('');
  const [trabajadorFavoritoId, setTrabajadorFavoritoId] = useState<number | null>(null);

  // Configurar meta tags Open Graph
  useEffect(() => {
    document.title = 'AlLado - Ofertas y Solicitudes';
    
    const metaTags = [
      { name: 'description', content: 'Plataforma de conexión entre empleadores y trabajadores' },
      { property: 'og:title', content: 'AlLado - Ofertas de empleo' },
      { property: 'og:description', content: 'Plataforma de conexión entre empleadores y trabajadores' },
      { property: 'og:type', content: 'website' },
    ];

    metaTags.forEach(({ name, property, content }) => {
      let meta = document.querySelector(`meta[${name ? `name="${name}"` : `property="${property}"`}]`) as HTMLMetaElement;
      if (!meta) {
        meta = document.createElement('meta');
        if (name) meta.setAttribute('name', name);
        if (property) meta.setAttribute('property', property);
        document.head.appendChild(meta);
      }
      meta.setAttribute('content', content);
    });

    return () => {
      // Cleanup: remove meta tags on unmount (optional)
    };
  }, []);

  useEffect(() => {
    let filtro = filtroEspecialidad;
    api.get('/oportunidades', { params: { filtro, page: 0, size: 10 } })
      .then((res) => {
        let datos = res.data;
        if (filtro) {
          datos = datos.filter((o: any) =>
            o.etiquetas?.some((et: string) => et.toLowerCase().includes(filtro.toLowerCase()))
          );
        }
        setOportunidades(datos);
      })
      .catch((err) => console.error('Error al cargar ofertas:', err))
      .finally(() => setLoading(false));
  }, [filtroEspecialidad]);

  if (loading) return <div className="inicio-screen"><p>Cargando publicaciones...</p></div>;

  return (
    <section className="inicio-screen">
      <header className="inicio-header">
        <span>AlLado</span>
        <span className="perfil" aria-label="Perfil de usuario" />
      </header>

      <select
        className="buscador"
        defaultValue=""
        onChange={(e) => setFiltroEspecialidad(e.target.value)}
      >
        <option value="">Buscar por Especialidad</option>
        <option>Electricidad</option>
        <option>Plomería</option>
        <option>Gas</option>
      </select>

      <h1 className="publicaciones-title">Publicaciones</h1>

      <div className="publicaciones-list">
        {oportunidades.map((op) => (
          <div key={op.id} className="oportunidad-item">
            <button
              className="publicacion-card"
              type="button"
              onClick={() => onSeleccionarOferta(op)}
            >
              <span className="publicacion-avatar" />
              <span className="publicacion-info">
                <strong>{op.autor?.nombre || 'Usuario'}</strong>
                <small>{op.titulo}</small>
                <br />
                <small>{op.ubicacion || '-'}</small>
                <br />
                <small>{op.modalidad || '-'}</small>
              </span>
            </button>
            <FavoriteButton
              trabajadorId={op.autor?.id || 1}
              onToggle={(isFavorite) => console.log('Favorite toggled:', isFavorite)}
            />
          </div>
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