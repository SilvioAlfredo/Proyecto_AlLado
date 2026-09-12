import { useState } from 'react';
import { api } from '../api/client';

interface CrearPostulacionProps {
  oportunidad: any;
  onVolver: () => void;
  onExito: () => void;
}

export function CrearPostulacionPage({ oportunidad, onVolver, onExito }: CrearPostulacionProps) {
  const [presupuesto, setPresupuesto] = useState('');
  const [disponibilidad, setDisponibilidad] = useState('');
  const [mensaje, setMensaje] = useState('');

  const handleEnviar = async () => {
    try {
      await api.post('/postulaciones', {
        titulo: `Postulación para ${oportunidad?.titulo || 'Oferta'}`,
        descripcion: mensaje,
        presupuesto: parseFloat(presupuesto) || 0,
        disponibilidad,
        autor: { id: 5 }, // Sin login, id hardocodeado para pruebas.
        oportunidad: { id: oportunidad?.id }
      });
      alert('¡Postulación realizada con éxito!');
      onExito();
    } catch (err) {
      alert('Error al enviar postulación');
    }
  };

  return (
    <section className="postulacion-screen">
      <button className="volver" type="button" onClick={onVolver}>
        <span className="volver-arrow">‹</span> Volver
      </button>

      <h1 className="screen-title">Postularse a la Solicitud</h1>

      <div className="detalle-box">
        <div className="detalle-content">
          {oportunidad?.descripcion || "Detalle de la necesidad no disponible."}
        </div>
      </div>

      <label className="field-label">Presupuesto Estimado</label>
      <input
        className="text-input"
        type="text"
        value={presupuesto}
        onChange={(e) => setPresupuesto(e.target.value)}
        placeholder="$15.000"
        required
      />

      <label className="field-label">Disponibilidad Estimada</label>
      <select
        className="select-input"
        value={disponibilidad}
        onChange={(e) => setDisponibilidad(e.target.value)}
      >
        <option value="">Seleccionar</option>
        <option value="Mañana">Mañana</option>
        <option value="Tarde">Tarde</option>
      </select>

      <label className="field-label">Mensaje (Opcional)</label>
      <div className="mensaje-box">
        <textarea
          value={mensaje}
          onChange={(e) => setMensaje(e.target.value)}
          maxLength={500}
          placeholder="Escribí un mensaje para el cliente..."
          required
        />
        <div className="contador">{mensaje.length}/500</div>
      </div>

      <button className="boton-enviar" type="button" onClick={handleEnviar}>
        Enviar Postulación
      </button>
    </section>
  );
}
