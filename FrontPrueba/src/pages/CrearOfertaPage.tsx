import { useState } from 'react';
import { api } from '../api/client';

interface CrearOfertaProps {
  onVolver: () => void;
  onExito: () => void;
}

export function CrearOfertaPage({ onVolver, onExito }: CrearOfertaProps) {
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/oportunidades', {
        titulo,
        descripcion,
        autor: { id: 1 } // ID hardcodeado para el MVP (Empleador test)
      });
      alert('¡Publicación creada!');
      onExito();
    } catch (err) {
      alert('Error al publicar la oferta');
    }
  };

  return (
    <section className="solicitud">
      <button className="volver" type="button" onClick={onVolver}>
        <span className="volver-arrow">←</span> Volver
      </button>

      <form onSubmit={handleSubmit}>
        <input
          className="titulo"
          type="text"
          placeholder="Nueva Solicitud"
          value={titulo}
          onChange={(e) => setTitulo(e.target.value)}
          required
        />

        <label htmlFor="descripcion">¿Qué necesitás?</label>
        <textarea
          id="descripcion"
          value={descripcion}
          onChange={(e) => setDescripcion(e.target.value)}
          required
        />

        <button className="publicar" type="submit">
          Publicar
        </button>
      </form>
    </section>
  );
}
