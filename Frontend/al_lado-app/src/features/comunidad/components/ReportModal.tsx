'use client';

import React, { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import {
  foroService,
  reportesService,
} from '@/features/comunidad/services/moderacionService';
import { errorMessage } from '@/lib/errors';
import { X, AlertTriangle } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface ReportModalProps {
  isOpen: boolean;
  onClose: () => void;
  targetId: number | null;
  onSuccess?: () => void;
}

export function ReportModal({ isOpen, onClose, targetId, onSuccess }: ReportModalProps) {
  const { userId } = useAuth();
  const enviar = useMutation({
    mutationFn: async () => {
      if (!userId || !targetId)
        throw new Error('No se pudo identificar el usuario o la publicación.');
      const [trabajador, publicacion] = await Promise.all([
        trabajadorService.obtenerPorId(userId),
        foroService.obtener(targetId),
      ]);
      return reportesService.crear({
        motivo,
        descripcion: `Publicación #${targetId}: ${publicacion.titulo}. ${detalles.trim() || motivo}`,
        reportante: { id: userId, nombre: trabajador.nombre, email: trabajador.email },
        reportado: publicacion.autor,
      });
    },
    onSuccess: () => {
      onSuccess?.();
      onClose();
    },
  });
  const [motivo, setMotivo] = useState('');
  const [detalles, setDetalles] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    enviar.mutate();
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-allado-card border border-allado-border rounded-2xl w-full max-w-md shadow-2xl animate-in fade-in zoom-in-95">
        <div className="p-5 border-b border-allado-border flex justify-between items-center bg-rose-400/10 rounded-t-2xl">
          <div className="flex items-center gap-2 text-rose-300">
            <AlertTriangle className="w-5 h-5" />
            <h3 className="font-bold text-lg">Reportar Contenido</h3>
          </div>
          <button onClick={onClose} disabled={enviar.isPending}>
            <X className="w-5 h-5 text-rose-400 hover:text-rose-300" />
          </button>
        </div>

        <form className="p-6 space-y-4" onSubmit={handleSubmit}>
          {enviar.error && (
            <p role="alert" className="text-red-300">
              {errorMessage(enviar.error)}
            </p>
          )}
          <p className="text-sm text-zinc-300 mb-4">
            Ayúdanos a mantener la comunidad segura. ¿Cuál es el problema con esta
            publicación o usuario?
          </p>

          <div>
            <label className="text-xs font-semibold text-zinc-200 block mb-1">
              Motivo del reporte
            </label>
            <select
              className="w-full text-white focus:outline-none focus:ring-2 focus:ring-allado-yellow text-sm border border-allado-border rounded-lg p-2.5 bg-allado-card2"
              value={motivo}
              onChange={(e) => setMotivo(e.target.value)}
              required
            >
              <option value="">Selecciona un motivo...</option>
              <option value="spam">Spam o contenido comercial</option>
              <option value="fraude">Intento de fraude / Estafa</option>
              <option value="lenguaje">Lenguaje inapropiado / Ofensivo</option>
              <option value="acoso">Acoso o discriminación</option>
            </select>
          </div>

          <div>
            <label className="text-xs font-semibold text-zinc-200 block mb-1">
              Detalles (Opcional)
            </label>
            <textarea
              rows={3}
              className="w-full bg-allado-card2 text-white placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-allado-yellow text-sm rounded-xl border border-allado-border p-3"
              placeholder="Añade más contexto para que los moderadores puedan revisarlo..."
              value={detalles}
              onChange={(e) => setDetalles(e.target.value)}
            />
          </div>

          <div className="pt-4 flex justify-end gap-2 border-t border-allado-border">
            <Button
              type="button"
              variant="outline"
              onClick={onClose}
              disabled={enviar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              disabled={enviar.isPending}
              className="bg-rose-600 hover:bg-rose-700 text-white"
            >
              Enviar Reporte
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
