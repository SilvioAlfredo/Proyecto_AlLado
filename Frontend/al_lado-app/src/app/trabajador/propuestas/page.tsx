'use client';

import { useState } from 'react';
import Link from 'next/link';
import { isAxiosError } from 'axios';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { OportunidadCard } from '@/features/oportunidades/components/OportunidadCard';
import { PostulacionModal } from '@/features/postulaciones/components/PostulacionModal';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import {
  TrabajadorPage,
  EstadoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import type { OportunidadLaboralDTO } from '@/types/trabajo';

export default function PropuestasProPage() {
  const consulta = useTrabajadorQuery('propuestas-pro', (id) =>
    oportunidadesService.propuestasPro(id),
  );
  const [selected, setSelected] = useState<OportunidadLaboralDTO | null>(null);
  const sinPlan = isAxiosError(consulta.error) && consulta.error.response?.status === 400;
  const totalPropuestas = consulta.data?.length ?? 0;
  const estadoPropuestas =
    totalPropuestas === 0
      ? 'Sin coincidencias'
      : `${totalPropuestas} propuesta${totalPropuestas === 1 ? '' : 's'} compatible${totalPropuestas === 1 ? '' : 's'}`;
  const siguientePaso =
    totalPropuestas === 0
      ? 'Completá tu perfil y disponibilidad para aumentar tus chances de aparecer en búsquedas.'
      : 'Revisá las oportunidades y postulá a las que mejor encajen con tu perfil.';

  return (
    <TrabajadorPage titulo="Propuestas PRO">
      {sinPlan ? (
        <section className={panelTrabajador}>
          <p>
            Necesitás una suscripción PRO vigente para acceder a propuestas compatibles
            con tus especialidades.
          </p>
          <Link href="/trabajador/suscripcion" className="text-allado-yellow underline">
            Ver mi suscripción
          </Link>
        </section>
      ) : (
        <>
          <EstadoTrabajador consulta={consulta} />
          <section className="mb-4 rounded-xl border border-allado-border bg-allado-bg/80 p-4">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-zinc-400">
              Estado de propuestas
            </p>
            <div className="mt-3 flex flex-wrap items-end justify-between gap-3">
              <div>
                <p className="text-2xl font-black text-white">{totalPropuestas}</p>
                <p className="text-sm text-zinc-400">coincidencias</p>
              </div>
              <span className="rounded-full bg-allado-yellow/10 px-2.5 py-1 text-xs font-bold text-allado-yellow">
                {estadoPropuestas}
              </span>
            </div>
            <p className="mt-3 text-sm text-zinc-300">{siguientePaso}</p>
          </section>
          {consulta.data?.length === 0 && (
            <section className={panelTrabajador}>
              <p>No hay propuestas compatibles con tu perfil todavía.</p>
              <div className="mt-3 flex flex-wrap gap-3">
                <Link href="/trabajador/disponibilidad" className="text-allado-yellow underline">
                  Actualizar disponibilidad
                </Link>
                <Link href="/trabajador/perfil" className="text-allado-yellow underline">
                  Completar mi perfil
                </Link>
              </div>
            </section>
          )}
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {consulta.data?.map((item) => (
              <OportunidadCard
                key={item.id}
                oportunidad={item}
                onPostularse={setSelected}
              />
            ))}
          </div>
        </>
      )}
      <PostulacionModal
        key={selected?.id ?? 'cerrado'}
        oportunidad={selected}
        isOpen={!!selected}
        onClose={() => setSelected(null)}
      />
    </TrabajadorPage>
  );
}
