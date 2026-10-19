'use client';

import { useState } from 'react';
import { useParams } from 'next/navigation';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, MapPin, DollarSign, Clock, User, Send } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PostulacionModal } from '@/features/postulaciones/components/PostulacionModal';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

export default function DetalleOportunidadPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const valid = Number.isSafeInteger(id) && id > 0;
  const [modalAbierto, setModalAbierto] = useState(false);
  const [enviada, setEnviada] = useState(false);
  const query = useQuery({
    queryKey: ['trabajador-oportunidad', id],
    queryFn: () => oportunidadesService.obtenerPorId(id),
    enabled: valid,
  });

  if (!valid) {
    return <p role="alert">El identificador de la oportunidad no es válido.</p>;
  }
  if (query.isPending) {
    return <p role="status">Cargando oportunidad…</p>;
  }
  if (query.isError) {
    return (
      <div className="space-y-3">
        <p role="alert">{errorMessage(query.error)}</p>
        <Button onClick={() => void query.refetch()}>Volver a intentar</Button>
      </div>
    );
  }

  const oportunidad = query.data;

  return (
    <div className="w-full">
      <div className="mx-auto max-w-3xl space-y-6">
        <div>
          <Link
            href="/trabajador/oportunidades"
            className="mb-4 inline-flex items-center text-sm text-zinc-400 hover:text-allado-yellow"
          >
            <ArrowLeft className="mr-1 h-4 w-4" /> Volver a Oportunidades
          </Link>
          <div className="mb-2 flex flex-wrap gap-2">
            {oportunidad.etiquetas?.map((etiqueta) => (
              <span
                key={etiqueta.id ?? etiqueta.nombre}
                className="rounded bg-allado-yellow/10 px-2 py-1 text-xs font-bold text-allado-yellow"
              >
                {etiqueta.nombre}
              </span>
            ))}
            {oportunidad.destacada && (
              <span className="rounded bg-allado-yellow/10 px-2 py-1 text-xs text-allado-yellow">
                Destacada
              </span>
            )}
          </div>
          <h1 className="text-2xl font-bold text-white md:text-3xl">
            {oportunidad.titulo}
          </h1>
          <p className="mt-2 flex items-center gap-1 text-sm text-zinc-400">
            <Clock className="h-4 w-4" /> {formatDate(oportunidad.fechaCreacion)}
          </p>
        </div>

        {enviada && (
          <p role="status" className="rounded-xl bg-emerald-400/10 p-3 text-emerald-300">
            Postulación enviada correctamente.
          </p>
        )}
        <div className="grid gap-6 md:grid-cols-3">
          <div className="space-y-6 md:col-span-2">
            <section className="rounded-2xl border border-allado-border bg-allado-card p-6 shadow-sm">
              <h2 className="mb-3 text-lg font-bold text-white">
                Descripción de la tarea
              </h2>
              <p className="whitespace-pre-line text-sm leading-relaxed text-zinc-300">
                {oportunidad.contenido}
              </p>
            </section>
            <section className="space-y-4 rounded-2xl border border-allado-border bg-allado-card p-6">
              <div className="flex items-start gap-3">
                <MapPin className="h-5 w-5 text-zinc-400" />
                <div>
                  <h2 className="text-xs font-semibold text-zinc-400">Ubicación</h2>
                  <p className="text-sm text-white">{oportunidad.ubicacion}</p>
                  <p className="text-sm text-zinc-400">{oportunidad.modalidad}</p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <DollarSign className="h-5 w-5 text-emerald-400" />
                <div>
                  <h2 className="text-xs font-semibold text-zinc-400">
                    Rango salarial publicado
                  </h2>
                  <p className="font-bold text-emerald-400">
                    {oportunidad.rangoSalarial || 'No informado'}
                  </p>
                </div>
              </div>
            </section>
          </div>
          <aside className="space-y-6">
            <section className="rounded-2xl border border-allado-border bg-allado-card p-6">
              <Button
                onClick={() => setModalAbierto(true)}
                disabled={!oportunidad.activa || enviada}
                className="w-full bg-allado-yellow text-black hover:bg-allado-yellow-hover"
              >
                <Send className="mr-2 h-4 w-4" />
                {!oportunidad.activa
                  ? 'Oferta cerrada'
                  : enviada
                    ? 'Enviada'
                    : 'Postularme ahora'}
              </Button>
              <p className="mt-3 text-xs text-zinc-400">
                Podés enviar una sola postulación a esta oferta mientras esté activa.
              </p>
            </section>
            <section className="rounded-2xl border border-allado-border bg-allado-card p-6">
              <h2 className="mb-4 font-bold text-white">Sobre el cliente</h2>
              <div className="flex items-center gap-3">
                <User className="h-6 w-6 text-zinc-400" />
                <p className="text-sm font-bold text-white">
                  {oportunidad.autor?.nombre || 'No informado'}
                </p>
              </div>
            </section>
          </aside>
        </div>
        <PostulacionModal
          key={id}
          isOpen={modalAbierto}
          onClose={() => setModalAbierto(false)}
          onSuccess={() => {
            setEnviada(true);
            setModalAbierto(false);
          }}
          oportunidadId={id}
          tituloOportunidad={oportunidad.titulo}
        />
      </div>
    </div>
  );
}
