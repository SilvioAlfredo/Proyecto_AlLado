'use client';

import { usePostulacionesTrabajador } from '@/features/postulaciones/hooks/usePostulacionesTrabajador';
import Link from 'next/link';
import { Badge } from '@/components/ui/badge';
import { formatDate } from '@/lib/utils';

export default function TrabajadorPostulacionesPage() {
  const { postulaciones, loading, error } = usePostulacionesTrabajador();

  if (error)
    return (
      <p role="alert" className="text-red-300">
        {error}
      </p>
    );
  if (loading) return <p className="text-zinc-400">Cargando postulaciones...</p>;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-extrabold text-white">Historial de Postulaciones</h1>
      {!postulaciones.length && <p>Sin postulaciones registradas.</p>}
      <div className="bg-[#1a1a1c] rounded-2xl border border-[#2e2e33] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-[#2e2e33]">
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Oportunidad
                </th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Remuneración Pretendida
                </th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Fecha</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Estado
                </th>
              </tr>
            </thead>
            <tbody>
              {postulaciones.map((p, index) => (
                <tr
                  key={p.id ?? index}
                  className="border-b border-[#2e2e33] last:border-0"
                >
                  <td className="p-4 font-medium text-white">
                    {p.id ? (
                      <Link
                        href={`/trabajador/postulaciones/${p.id}`}
                        className="hover:text-[#ffc107]"
                      >
                        {p.oportunidadLaboral?.titulo || p.titulo}
                      </Link>
                    ) : (
                      p.oportunidadLaboral?.titulo || p.titulo
                    )}
                  </td>
                  <td className="p-4 text-zinc-300">${p.remuneracionPretendida}</td>
                  <td className="p-4 text-zinc-400">
                    {p.fechaCreacion ? formatDate(p.fechaCreacion) : '-'}
                  </td>
                  <td className="p-4">
                    <Badge
                      variant={
                        p.estado === 'SELECCIONADA'
                          ? 'success'
                          : p.estado === 'RECHAZADA'
                            ? 'destructive'
                            : 'warning'
                      }
                    >
                      {p.estado}
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
