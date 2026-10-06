'use client';

import { useState } from 'react';
import { OportunidadLaboralDTO } from '@/types/trabajo';
import { postulacionesService } from '../services/postulacionesService';
import { Dialog } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

interface PostulacionModalProps {
  oportunidad: OportunidadLaboralDTO | null;
  isOpen: boolean;
  onClose: () => void;
}

export function PostulacionModal({ oportunidad, isOpen, onClose }: PostulacionModalProps) {
  const [contenido, setContenido] = useState('');
  const [remuneracion, setRemuneracion] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!oportunidad) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await postulacionesService.crear({
        titulo: `Postulación a: ${oportunidad.titulo}`,
        contenido,
        remuneracionPretendida: Number(remuneracion),
        oportunidadLaboral: {
          id: oportunidad.id!,
          titulo: oportunidad.titulo,
          contenido: oportunidad.contenido,
          ubicacion: oportunidad.ubicacion,
          modalidad: oportunidad.modalidad,
          rangoSalarial: oportunidad.rangoSalarial,
          activa: oportunidad.activa,
          fechaCreacion: oportunidad.fechaCreacion || '',
        },
      });
      setContenido('');
      setRemuneracion('');
      onClose();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Error al enviar la postulación. Requiere rol TRABAJADOR.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog isOpen={isOpen} onClose={onClose} title={`Postularse a: ${oportunidad.titulo}`}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-400">{error}</div>}

        <div>
          <label className="mb-1 block text-sm font-medium text-zinc-300">Mensaje / Propuesta (máx 500)</label>
          <textarea
            required
            rows={4}
            maxLength={500}
            value={contenido}
            onChange={(e) => setContenido(e.target.value)}
            className="w-full rounded-2xl border border-[#3a3a3f] bg-[#26262a] p-3 text-sm text-white placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-[#ffc107]"
            placeholder="Tengo experiencia en... Puedo pasar mañana y solucionarlo."
          />
          <p className="mt-1 text-right text-xs text-zinc-500">{contenido.length}/500</p>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-zinc-300">Presupuesto ($) + Disponibilidad</label>
          <Input
            type="number"
            required
            value={remuneracion}
            onChange={(e) => setRemuneracion(e.target.value)}
            placeholder="Ej: 15000"
          />
        </div>

        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button type="submit" disabled={loading}>
            {loading ? 'Enviando...' : 'Enviar Postulación'}
          </Button>
        </div>
        <p className="text-xs text-zinc-600">POST /postulaciones · requiere ROLE_TRABAJADOR</p>
      </form>
    </Dialog>
  );
}
