'use client';

import { useState } from 'react';
import { Dialog } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { obtenerRefSesion } from '@/lib/usuarioRef';

interface OportunidadFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export function OportunidadFormModal({ isOpen, onClose, onSuccess }: OportunidadFormModalProps) {
  const [titulo, setTitulo] = useState('');
  const [contenido, setContenido] = useState('');
  const [ubicacion, setUbicacion] = useState('');
  const [modalidad, setModalidad] = useState('PRESENCIAL');
  const [rangoSalarial, setRangoSalarial] = useState('');
  const [destacada, setDestacada] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const autor = obtenerRefSesion();
      if (!autor?.id) throw new Error('No se encontró tu usuario. Iniciá sesión de nuevo.');
      await oportunidadesService.crear({
        titulo,
        contenido,
        autor,
        ubicacion,
        modalidad,
        rangoSalarial,
        activa: true,
        destacada,
        etiquetas: [],
      });
      onSuccess();
      onClose();
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || 'No se pudo publicar.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog isOpen={isOpen} onClose={onClose} title="Publicar Nueva Oportunidad Laboral">
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-400">{error}</div>}
        <div>
          <label className="block text-sm font-medium mb-1">Título del puesto o tarea</label>
          <Input required value={titulo} onChange={(e) => setTitulo(e.target.value)} placeholder="Ej: Reparación eléctrica doméstica" />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Descripción detallada</label>
          <textarea
            required
            rows={3}
            value={contenido}
            onChange={(e) => setContenido(e.target.value)}
            className="w-full rounded-xl bg-[#0b0b0c] border border-[#2e2e33] p-3 text-sm text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
            placeholder="Detalla los requerimientos o necesidades..."
          />
        </div>
        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="block text-sm font-medium mb-1">Ubicación</label>
            <Input required value={ubicacion} onChange={(e) => setUbicacion(e.target.value)} placeholder="Ej: Barrio Centro" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Presupuesto / Rango</label>
            <Input required value={rangoSalarial} onChange={(e) => setRangoSalarial(e.target.value)} placeholder="Ej: $15.000 - $25.000" />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="block text-sm font-medium mb-1">Modalidad</label>
            <select
              value={modalidad}
              onChange={(e) => setModalidad(e.target.value)}
              className="h-11 w-full rounded-xl border border-[#2e2e33] bg-[#0b0b0c] px-3 text-sm text-white"
            >
              <option value="PRESENCIAL">Presencial</option>
              <option value="REMOTO">Remoto</option>
              <option value="HIBRIDO">Híbrido</option>
            </select>
          </div>
          <label className="flex items-center gap-2 pt-6 text-sm text-zinc-300">
            <input type="checkbox" checked={destacada} onChange={(e) => setDestacada(e.target.checked)} />
            Destacada <span className="text-zinc-500">(requiere plan PRO)</span>
          </label>
        </div>
        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button type="submit" disabled={loading}>{loading ? 'Guardando...' : 'Publicar'}</Button>
        </div>
      </form>
    </Dialog>
  );
}