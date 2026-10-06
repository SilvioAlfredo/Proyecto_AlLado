'use client';

import { useState } from 'react';
import { Dialog } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';

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
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await oportunidadesService.crear({
        titulo,
        contenido,
        ubicacion,
        modalidad,
        rangoSalarial,
        activa: true,
        etiquetas: [],
      });
      onSuccess();
      onClose();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog isOpen={isOpen} onClose={onClose} title="Publicar Nueva Oportunidad Laboral">
      <form onSubmit={handleSubmit} className="space-y-4">
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
        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button type="submit" disabled={loading}>{loading ? 'Guardando...' : 'Publicar'}</Button>
        </div>
      </form>
    </Dialog>
  );
}