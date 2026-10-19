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
          {consulta.data?.length === 0 && (
            <p>Todavía no hay propuestas compatibles con tu perfil.</p>
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
