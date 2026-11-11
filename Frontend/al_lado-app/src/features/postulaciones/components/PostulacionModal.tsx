'use client';

import { useQueryClient } from '@tanstack/react-query';
import React, { useState } from 'react';
import { X, DollarSign, Calendar, Send, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import type { OportunidadLaboralDTO } from '@/types/trabajo';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { obtenerRefPorEmail } from '@/lib/usuarioRef';
import { errorMessage } from '@/lib/errors';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { postulacionesService } from '../services/postulacionesService';

interface PostulacionModalProps {
  oportunidadId?: number;
  oportunidad?: OportunidadLaboralDTO | null;
  tituloOportunidad?: string;
  presupuestoReferencia?: number;
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

export function PostulacionModal({
  oportunidadId,
  oportunidad,
  tituloOportunidad,
  presupuestoReferencia,
  isOpen,
  onClose,
  onSuccess,
}: PostulacionModalProps) {
  const { user } = useAuth();
  const cache = useQueryClient();
  const id = oportunidadId ?? oportunidad?.id;
  const titulo = tituloOportunidad ?? oportunidad?.titulo ?? '';
  const [tarifaPropuesta, setTarifaPropuesta] = useState(
    presupuestoReferencia ? presupuestoReferencia.toString() : '',
  );
  const [tiempoEstimado, setTiempoEstimado] = useState('1');
  const [mensaje, setMensaje] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!tarifaPropuesta || !mensaje.trim()) {
      setErrorMsg('Por favor completa tu propuesta económica y mensaje.');
      return;
    }

    try {
      setEnviando(true);
      setErrorMsg(null);
      if (
        !user?.roles.some((rol) => rol === 'ROLE_TRABAJADOR' || rol === 'TRABAJADOR') ||
        !id
      ) {
        throw new Error('Inicia sesión como trabajador para postularte.');
      }
      const autor = await obtenerRefPorEmail(user.email);
      if (!autor?.id) throw new Error('No se pudo identificar tu usuario.');
      const oferta = oportunidad ?? (await oportunidadesService.obtenerPorId(id));
      const monto = Number(tarifaPropuesta);
      const dias = Number(tiempoEstimado);
      if (!Number.isFinite(monto) || monto < 0 || !Number.isInteger(dias) || dias < 1) {
        throw new Error('Revisa el monto y el plazo de tu propuesta.');
      }
      await postulacionesService.crear({
        titulo: `Propuesta para ${titulo}`.slice(0, 150),
        contenido: `${mensaje.trim()}\n\nPlazo estimado: ${dias} días.`,
        autor,
        remuneracionPretendida: monto,
        oportunidadLaboral: { ...oferta, id, fechaCreacion: oferta.fechaCreacion ?? '' },
      });
      await cache.invalidateQueries({ queryKey: ['trabajador'] });
      setTarifaPropuesta('');
      setMensaje('');
      setTiempoEstimado('1');
      if (onSuccess) onSuccess();
      else onClose();
    } catch (err: unknown) {
      setErrorMsg(errorMessage(err, 'Ocurrió un error al enviar tu postulación.'));
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-allado-card rounded-2xl w-full max-w-lg shadow-2xl border border-allado-border overflow-hidden animate-in fade-in zoom-in-95">
        {/* Cabecera */}
        <div className="p-5 border-b border-allado-border flex items-center justify-between">
          <div>
            <h3 className="font-bold text-white text-lg">Enviar Postulación</h3>
            <p className="text-xs text-zinc-400 line-clamp-1">{titulo}</p>
          </div>
          <button onClick={onClose} className="text-zinc-400 hover:text-zinc-300">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Formulario */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {errorMsg && (
            <div className="flex items-center gap-2 p-3 bg-red-400/10 text-red-300 text-xs rounded-lg border border-red-400/30">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{errorMsg}</span>
            </div>
          )}

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-semibold text-zinc-200 block mb-1">
                Tu cotización ($)
              </label>
              <div className="relative">
                <DollarSign className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-zinc-400" />
                <Input
                  type="number"
                  min="0"
                  step="0.01"
                  className="pl-9"
                  placeholder="Ej. 18000"
                  value={tarifaPropuesta}
                  onChange={(e) => setTarifaPropuesta(e.target.value)}
                  required
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-zinc-200 block mb-1">
                Plazo estimado (días)
              </label>
              <div className="relative">
                <Calendar className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-zinc-400" />
                <Input
                  type="number"
                  min="1"
                  className="pl-9"
                  value={tiempoEstimado}
                  onChange={(e) => setTiempoEstimado(e.target.value)}
                  required
                />
              </div>
            </div>
          </div>

          <div>
            <label className="text-xs font-semibold text-zinc-200 block mb-1">
              Mensaje o propuesta para el cliente
            </label>
            <textarea
              rows={4}
              className="w-full bg-allado-card2 text-white placeholder:text-zinc-500 text-sm rounded-xl border border-allado-border p-3 focus:outline-none focus:ring-2 focus:ring-allado-yellow focus:border-transparent resize-none"
              placeholder="Explica tu experiencia previa, herramientas que utilizas o disponibilidad horaria..."
              value={mensaje}
              onChange={(e) => setMensaje(e.target.value)}
              required
            />
          </div>

          <div className="flex items-center justify-end gap-2 pt-3 border-t border-allado-border">
            <Button type="button" variant="outline" onClick={onClose} disabled={enviando}>
              Cancelar
            </Button>
            <Button
              type="submit"
              className="bg-allado-yellow hover:bg-allado-yellow-hover text-black flex items-center gap-1.5"
              disabled={enviando}
            >
              <Send className="w-4 h-4" />
              <span>{enviando ? 'Enviando...' : 'Confirmar Postulación'}</span>
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
