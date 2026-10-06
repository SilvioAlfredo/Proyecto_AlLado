'use client';

import { useState } from 'react';
import { Dialog } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';

interface ReportModalProps {
  publicacionId: number | null;
  isOpen: boolean;
  onClose: () => void;
}

export function ReportModal({ publicacionId, isOpen, onClose }: ReportModalProps) {
  const [motivo, setMotivo] = useState('');
  const [loading, setLoading] = useState(false);

  if (!publicacionId) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    // Simulación de envío de reporte
    setTimeout(() => {
      setLoading(false);
      setMotivo('');
      onClose();
    }, 500);
  };

  return (
    <Dialog isOpen={isOpen} onClose={onClose} title="Reportar Publicación">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Motivo del reporte</label>
          <textarea
            required
            rows={3}
            value={motivo}
            onChange={(e) => setMotivo(e.target.value)}
            className="w-full rounded-xl bg-[#0b0b0c] border border-[#2e2e33] p-3 text-sm text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
            placeholder="Especifica por qué reportas esta publicación..."
          />
        </div>
        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="destructive" disabled={loading}>
            {loading ? 'Enviando...' : 'Enviar Reporte'}
          </Button>
        </div>
      </form>
    </Dialog>
  );
}