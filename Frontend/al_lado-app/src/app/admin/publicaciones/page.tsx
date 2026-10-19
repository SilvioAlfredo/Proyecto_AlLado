'use client';

import { useState, useEffect } from 'react';
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { comunidadService } from '@/features/comunidad/services/comunidadService';
import { formatDate } from '@/lib/utils';

interface ReporteItem {
  id: string;
  tipo: string;
  contenido: string;
  motivoReporte: string;
  createdAt: string;
  autorNombre: string;
  estatus: string;
}

export default function AdminPublicacionesPage() {
  const [reportes, setReportes] = useState<ReporteItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filtroEstatus, setFiltroEstatus] = useState('');

  useEffect(() => {
    const cargar = async () => {
      setLoading(true);
      try {
        const data = await comunidadService.obtenerReportes();
        setReportes(
          data.map((r: any) => ({
            id: r.id?.toString() || '',
            tipo: r.tipo || 'reporte',
            contenido: r.contenido || r.descripcion || 'Sin contenido',
            motivoReporte: r.motivo || 'No especificado',
            createdAt: r.fechaCreacion || new Date().toISOString(),
            autorNombre: r.autor?.nombre || 'Anónimo',
            estatus: r.estado || 'PENDIENTE',
          })),
        );
      } catch (err: any) {
        setError(err?.response?.data?.message || 'Error al cargar reportes');
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  const filtrados = filtroEstatus
    ? reportes.filter((r) => r.estatus === filtroEstatus)
    : reportes;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-extrabold text-white">Panel de Moderación</h1>
        <p className="text-sm text-zinc-500">
          Gestión de reportes · GET /reportes · PUT /reportes/{'{id}'}/resolver (ADMIN)
        </p>
      </div>

      {error && (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-red-400">
          <p className="font-medium">Error: {error}</p>
        </div>
      )}

      <div className="mb-4 max-w-xs">
        <select
          value={filtroEstatus}
          onChange={(e) => setFiltroEstatus(e.target.value)}
          className={
            'h-11 w-full rounded-full border border-[#3a3a3f] ' +
            'bg-[#26262a] px-4 text-sm text-white'
          }
        >
          <option value="">Todos</option>
          <option value="PENDIENTE">Pendientes</option>
          <option value="EN_REVISION">En revisión</option>
          <option value="RESUELTO">Resueltos</option>
          <option value="DESCARTADO">Descartados</option>
        </select>
      </div>

      <div className="bg-[#1a1a1c] rounded-2xl border border-[#2e2e33] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-[#2e2e33]">
                <th className="text-left p-4 text-sm font-medium text-zinc-500">ID</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Tipo</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Contenido
                </th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Motivo
                </th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Autor</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Fecha</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Estatus
                </th>
              </tr>
            </thead>
            <tbody>
              {filtrados.map((reporte) => (
                <tr key={reporte.id} className="border-b border-[#2e2e33] last:border-0">
                  <td className="p-4 text-zinc-400">{reporte.id}</td>
                  <td className="p-4 text-zinc-300">{reporte.tipo}</td>
                  <td className="p-4 text-zinc-300">{reporte.contenido}</td>
                  <td className="p-4 text-zinc-300">{reporte.motivoReporte}</td>
                  <td className="p-4 text-zinc-300">{reporte.autorNombre}</td>
                  <td className="p-4 text-zinc-400">{formatDate(reporte.createdAt)}</td>
                  <td className="p-4">
                    <Badge
                      variant={reporte.estatus === 'PENDIENTE' ? 'warning' : 'default'}
                    >
                      {reporte.estatus}
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {loading && <p className="text-zinc-500">Cargando reportes...</p>}
    </div>
  );
}
