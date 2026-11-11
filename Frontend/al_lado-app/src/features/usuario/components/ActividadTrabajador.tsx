'use client';

import React from 'react';
import {
  CheckCircle2,
  Clock,
  Star,
  DollarSign,
  Activity,
  ArrowUpRight,
} from 'lucide-react';
import { useEstadisticasTrabajador } from '@/features/usuario/hooks/useEstadisticasTrabajador';
import { usePostulacionesTrabajador } from '@/features/postulaciones/hooks/usePostulacionesTrabajador';
import { EstadoTrabajador } from '@/features/usuario/components/TrabajadorPage';
import { formatDate } from '@/lib/utils';
import { Card } from '@/components/ui/card';

export function ActividadTrabajador() {
  const consulta = useEstadisticasTrabajador();
  const { postulaciones, loading, error } = usePostulacionesTrabajador();
  const datos = consulta.data;
  if (!datos) return <EstadoTrabajador consulta={consulta} />;
  const estadisticas = {
    trabajosCompletados: datos.postulacionesCompletadas,
    gananciasMes: datos.ingresosUltimos3Meses,
    postulacionesActivas:
      loading || error
        ? '—'
        : postulaciones.filter((p) => ['PENDIENTE', 'SELECCIONADA'].includes(p.estado))
            .length,
    reputacion: datos.promedioValoraciones,
  };
  const actividadReciente = [...postulaciones]
    .sort((a, b) => (b.fechaCreacion ?? '').localeCompare(a.fechaCreacion ?? ''))
    .slice(0, 10)
    .map((p) => ({
      id: p.id,
      descripcion: `${p.titulo} · ${p.estado}`,
      fecha: formatDate(p.fechaCreacion),
    }));

  return (
    <div className="w-full">
      <div className="max-w-5xl mx-auto space-y-6">
        <div>
          <h1 className="text-2xl md:text-3xl font-bold text-white">
            Hola, {datos.nombre}
          </h1>
          <p className="text-sm text-zinc-400">
            Aquí tienes el resumen de tu actividad y rendimiento en AlLado.
          </p>
        </div>

        {/* KPIs */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="bg-allado-card p-5 border-allado-border shadow-sm flex flex-col justify-center">
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2 bg-emerald-400/10 text-emerald-400 rounded-lg">
                <DollarSign className="w-5 h-5" />
              </div>
              <span className="text-sm font-medium text-zinc-300">
                Ingresos de los últimos 3 meses
              </span>
            </div>
            <div className="flex items-baseline gap-2">
              <h3 className="text-2xl font-bold text-white">
                ${estadisticas.gananciasMes.toLocaleString()}
              </h3>
            </div>
          </Card>

          <Card className="bg-allado-card p-5 border-allado-border shadow-sm flex flex-col justify-center">
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2 bg-allado-yellow/10 text-allado-yellow rounded-lg">
                <CheckCircle2 className="w-5 h-5" />
              </div>
              <span className="text-sm font-medium text-zinc-300">Trabajos listos</span>
            </div>
            <h3 className="text-2xl font-bold text-white">
              {estadisticas.trabajosCompletados}
            </h3>
          </Card>

          <Card className="bg-allado-card p-5 border-allado-border shadow-sm flex flex-col justify-center">
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2 bg-allado-yellow/10 text-allado-yellow rounded-lg">
                <Clock className="w-5 h-5" />
              </div>
              <span className="text-sm font-medium text-zinc-300">Postulaciones</span>
            </div>
            <h3 className="text-2xl font-bold text-white">
              {estadisticas.postulacionesActivas}{' '}
              <span className="text-sm font-normal text-zinc-400">activas</span>
            </h3>
          </Card>

          <Card className="bg-allado-card p-5 border-allado-border shadow-sm flex flex-col justify-center">
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2 bg-purple-400/10 text-purple-300 rounded-lg">
                <Star className="w-5 h-5" />
              </div>
              <span className="text-sm font-medium text-zinc-300">Reputación</span>
            </div>
            <h3 className="text-2xl font-bold text-white">
              {estadisticas.reputacion}{' '}
              <span className="text-sm font-normal text-zinc-400">/ 5.0</span>
            </h3>
          </Card>
        </div>

        {/* Historial Reciente */}
        <div className="bg-allado-card rounded-2xl border border-allado-border shadow-sm overflow-hidden mt-8">
          <div className="p-5 border-b border-allado-border flex items-center gap-2">
            <Activity className="w-5 h-5 text-zinc-200" />
            <h2 className="text-lg font-bold text-white">Actividad Reciente</h2>
          </div>
          <div className="divide-y divide-allado-border">
            {loading && <p className="p-5">Cargando actividad…</p>}
            {error && (
              <p role="alert" className="p-5 text-red-300">
                {error}
              </p>
            )}
            {!loading && !error && !actividadReciente.length && (
              <p className="p-5">Sin postulaciones registradas.</p>
            )}
            {actividadReciente.map((item) => (
              <div
                key={item.id}
                className="p-5 flex items-center justify-between hover:bg-allado-bg transition-colors"
              >
                <div className="flex items-start gap-3">
                  <ArrowUpRight className="w-4 h-4 text-allado-yellow" />
                  <div>
                    <p className="text-sm font-medium text-white">{item.descripcion}</p>
                    <p className="text-xs text-zinc-400 mt-0.5">{item.fecha}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
