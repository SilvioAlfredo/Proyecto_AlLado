'use client';

import React from 'react';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import { valoracionesService } from '@/features/comunidad/services/valoracionesService';
import { EstadoTrabajador } from '@/features/usuario/components/TrabajadorPage';
import { formatDate } from '@/lib/utils';
import { Star, MessageSquare } from 'lucide-react';

export default function ValoracionesPage() {
  const consulta = useTrabajadorQuery('valoraciones', (id) =>
    valoracionesService.porTrabajador(id),
  );
  if (!consulta.data) return <EstadoTrabajador consulta={consulta} />;
  const datos = consulta.data;
  const totalResenas = datos.length;
  const promedio = totalResenas
    ? Number(
        (datos.reduce((suma, v) => suma + v.puntuacion, 0) / totalResenas).toFixed(1),
      )
    : 0;
  const distribucion = [5, 4, 3, 2, 1].map((estrellas) => ({
    estrellas,
    cantidad: datos.filter((v) => v.puntuacion === estrellas).length,
  }));
  const resenas = [...datos]
    .sort((a, b) => (b.fechaCreacion ?? '').localeCompare(a.fechaCreacion ?? ''))
    .map((v) => ({
      id: v.id,
      empleador: v.autor?.nombre ?? 'Autor no disponible',
      fecha: formatDate(v.fechaCreacion),
      calificacion: v.puntuacion,
      comentario: v.comentario ?? 'Sin comentario',
    }));

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }).map((_, idx) => (
      <Star
        key={idx}
        className={`w-4 h-4 ${idx < rating ? 'text-allado-yellow fill-allado-yellow' : 'text-zinc-700 fill-zinc-700'}`}
      />
    ));
  };

  return (
    <div className="w-full">
      <div className="max-w-4xl mx-auto space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Mis Valoraciones</h1>
          <p className="text-sm text-zinc-400">
            Descubre lo que opinan los empleadores sobre tus servicios
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Resumen de Calificación */}
          <div className="md:col-span-1 bg-allado-card rounded-2xl border border-allado-border p-6 shadow-sm flex flex-col items-center justify-center text-center">
            <h2 className="text-5xl font-black text-white mb-2">{promedio}</h2>
            <div className="flex gap-1 mb-2">{renderStars(Math.round(promedio))}</div>
            <p className="text-sm text-zinc-400 font-medium">
              {totalResenas} valoraciones totales
            </p>
          </div>

          {/* Barras de Distribución */}
          <div className="md:col-span-2 bg-allado-card rounded-2xl border border-allado-border p-6 shadow-sm flex flex-col justify-center space-y-3">
            {distribucion.map((dist) => (
              <div key={dist.estrellas} className="flex items-center gap-3">
                <span className="w-12 text-sm font-semibold text-zinc-300 flex items-center gap-1">
                  {dist.estrellas}{' '}
                  <Star className="w-3.5 h-3.5 text-zinc-400 fill-zinc-400" />
                </span>
                <div className="flex-1 h-2.5 bg-allado-card2 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-allado-yellow rounded-full"
                    style={{
                      width: `${(totalResenas ? dist.cantidad / totalResenas : 0) * 100}%`,
                    }}
                  />
                </div>
                <span className="w-8 text-right text-sm text-zinc-400">
                  {dist.cantidad}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Lista de Reseñas */}
        <div className="bg-allado-card rounded-2xl border border-allado-border shadow-sm overflow-hidden mt-6">
          <div className="p-5 border-b border-allado-border flex items-center gap-2">
            <MessageSquare className="w-5 h-5 text-zinc-200" />
            <h3 className="font-bold text-lg text-white">Comentarios Recientes</h3>
          </div>

          <div className="divide-y divide-allado-border">
            {!resenas.length && (
              <p className="p-6 text-zinc-400">Todavía no recibiste valoraciones.</p>
            )}
            {resenas.map((resena) => (
              <div key={resena.id} className="p-6 hover:bg-allado-bg transition-colors">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <h4 className="font-bold text-white">{resena.empleador}</h4>
                    <span className="text-xs text-zinc-400">{resena.fecha}</span>
                  </div>
                  <div className="flex gap-0.5">{renderStars(resena.calificacion)}</div>
                </div>
                <p className="text-sm text-zinc-300 leading-relaxed bg-allado-bg p-4 rounded-xl border border-allado-border">
                  {resena.comentario}
                </p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
