import { useState } from 'react';
import { api } from '../api/client';

interface CrearOfertaProps {
  onVolver: () => void;
  onExito: () => void;
}

export function CrearOfertaPage({ onVolver, onExito }: CrearOfertaProps) {
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [ubicacion, setUbicacion] = useState('');
  const [modalidad, setModalidad] = useState('REMOTO');
  const [rangoSalarial, setRangoSalarial] = useState('');

  // Obtener ID del usuario logueado desde localStorage
  const getUsuarioId = () => {
    const user = localStorage.getItem('user');
    if (user) {
      try {
        return JSON.parse(user).id;
      } catch {
        return null;
      }
    }
    return null;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const usuarioId = getUsuarioId();
    if (!usuarioId) {
      alert('Debes iniciar sesión para publicar una oferta');
      return;
    }

    try {
      await api.post('/oportunidades', {
        titulo,
        descripcion,
        ubicacion,
        modalidad,
        rangoSalarial,
        autor: { id: usuarioId }
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

        <label htmlFor="ubicacion">Ubicación</label>
        <input
          className="texto"
          type="text"
          placeholder="Ciudad o zona"
          value={ubicacion}
          onChange={(e) => setUbicacion(e.target.value)}
        />

        <label htmlFor="modalidad">Modalidad</label>
        <select
          className="select-input"
          value={modalidad}
          onChange={(e) => setModalidad(e.target.value)}
        >
          <option value="REMOTO">Remoto</option>
          <option value="PRESENCIAL">Presencial</option>
          <option value="HIBRIDO">Híbrido</option>
        </select>

        <label htmlFor="rangoSalarial">Rango salarial</label>
        <input
          className="texto"
          type="text"
          placeholder="Ej: $10.000 - $20.000"
          value={rangoSalarial}
          onChange={(e) => setRangoSalarial(e.target.value)}
        />

        <button className="publicar" type="submit">
          Publicar
        </button>
      </form>
    </section>
  );
}
