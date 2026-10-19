'use client';

import Link from 'next/link';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { reportesService } from '@/features/comunidad/services/moderacionService';
import { Feedback, LoadError } from './Feedback';
import { PageHeader, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

/**
 * Permite revisar un reporte y pasar a revisión, resolverlo o descartarlo.
 *
 * Los estados RESUELTO y DESCARTADO se presentan sin acciones de reapertura.
 * El backend utiliza al administrador autenticado como moderador.
 *
 * @param props - Opciones del detalle.
 * @param props.id - Identificador del reporte.
 */
export function ReporteAdminDetail({ id }: { id: number }) {
  const valid = Number.isSafeInteger(id) && id > 0;
  const client = useQueryClient();
  const query = useQuery({
    queryKey: ['admin-reporte', id],
    enabled: valid,
    retry: false,
    queryFn: () => reportesService.obtener(id),
  });
  const mutation = useMutation({
    mutationFn: (estado: 'EN_REVISION' | 'RESUELTO' | 'DESCARTADO') =>
      reportesService.resolver(id, estado),
    onSuccess: (data) => {
      client.setQueryData(['admin-reporte', id], data);
      client.invalidateQueries({ queryKey: ['admin-reportes'] });
    },
  });

  if (!valid) {
    return <Feedback message="El identificador del reporte no es válido." />;
  }
  if (query.isPending) {
    return <p role="status">Cargando reporte…</p>;
  }
  if (query.isError) {
    return (
      <LoadError message={errorMessage(query.error)} retry={() => void query.refetch()} />
    );
  }
  const report = query.data;
  const closed = ['RESUELTO', 'DESCARTADO'].includes(report.estado ?? '');

  return (
    <div className="space-y-6">
      <Link
        href={`/admin/reportes?estado=${report.estado ?? 'TODOS'}`}
        className="text-sm text-zinc-400"
      >
        ← Volver a Reportes
      </Link>
      <PageHeader
        eyebrow="Moderación"
        title="Detalle del reporte"
        subtitle={`Reporte #${report.id} · ${formatDate(report.fechaCreacion)}`}
      />
      {mutation.isError && <Feedback message={errorMessage(mutation.error)} />}
      {mutation.isSuccess && (
        <Feedback success message="Estado del reporte actualizado." />
      )}
      <div className="grid gap-5 lg:grid-cols-[1fr_300px]">
        <section className="space-y-5">
          <div className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
            <p className="text-xs text-[#ffc107]">{report.estado}</p>
            <h2 className="text-lg font-bold">{report.motivo}</h2>
            <h3 className="font-semibold">Descripción</h3>
            <p className="whitespace-pre-wrap text-sm text-zinc-300">
              {report.descripcion}
            </p>
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            {[
              { label: 'Reportante', person: report.reportante },
              { label: 'Reportado', person: report.reportado },
            ].map(({ label, person }) => (
              <div
                key={label}
                className="space-y-2 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
              >
                <h2 className="text-sm font-bold">{label}</h2>
                <p>{person?.nombre || 'No especificado'}</p>
                <p className="break-all text-sm text-zinc-400">{person?.email}</p>
              </div>
            ))}
          </div>
        </section>
        <aside className="h-fit space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
          <p className="text-xs font-bold uppercase text-[#ffc107]">
            Gestión del reporte
          </p>
          <h2 className="font-bold">
            {closed ? 'Reporte cerrado' : 'Actualizar estado'}
          </h2>
          <p className="text-sm text-zinc-400">
            Moderador: {report.moderador?.nombre || 'Todavía sin asignar'}
          </p>
          {closed ? (
            <Note>Este reporte ya está cerrado. Su estado no puede modificarse.</Note>
          ) : (
            <>
              <p className="text-sm text-zinc-400">
                El cambio quedará registrado a nombre del administrador de esta sesión.
              </p>
              {report.estado === 'PENDIENTE' && (
                <Button
                  className="w-full"
                  disabled={mutation.isPending}
                  onClick={() => mutation.mutate('EN_REVISION')}
                >
                  Pasar a En revisión
                </Button>
              )}
              <div className="flex gap-2">
                <Button
                  disabled={mutation.isPending}
                  onClick={() => {
                    if (confirm('Resolver este reporte de forma definitiva?')) {
                      mutation.mutate('RESUELTO');
                    }
                  }}
                >
                  Resolver
                </Button>
                <Button
                  variant="outline"
                  disabled={mutation.isPending}
                  onClick={() => {
                    if (confirm('Descartar este reporte de forma definitiva?')) {
                      mutation.mutate('DESCARTADO');
                    }
                  }}
                >
                  Descartar
                </Button>
              </div>
              <Note tone="danger">
                Resolver o descartar es definitivo. Confirmá que revisaste el caso.
              </Note>
            </>
          )}
        </aside>
      </div>
    </div>
  );
}
