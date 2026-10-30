'use client';
import { useQuery } from '@tanstack/react-query';
import { isAxiosError } from 'axios';
import Link from 'next/link';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { suscripcionesService } from '@/features/usuario/services/aportesService';
import { subscriptionState } from '@/features/admin/lib/aportes';
import { fecha, panel, QueryStatus } from '@/features/empleador/components/EmployerUI';
import { PageHeader, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
export default function PlanPage() {
  const { userId } = useAuth();
  const query = useQuery({
    queryKey: ['empleador', userId, 'plan'],
    enabled: !!userId,
    queryFn: async () => {
      try {
        return await suscripcionesService.porUsuario(userId!);
      } catch (e) {
        if (isAxiosError(e) && e.response?.status === 404) return null;
        throw e;
      }
    },
  });
  const sub = query.data;
  const pro = sub?.nivel === 'PRO' && subscriptionState(sub) === 'Activa y vigente';
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Mi plan"
        title="Un plan para tus proyectos."
        subtitle="Consultá tu suscripción y registrá tus aportes."
      />
      <QueryStatus pending={query.isPending} error={query.error} />
      {!query.isPending && !query.error && (
        <>
          <div className="grid gap-5 lg:grid-cols-2">
            <section className={`${panel} border-[#ffc107]/50 bg-[#ffc107]/10`}>
              <p className="text-sm text-[#ffc107]">
                Tu plan actual · {sub ? subscriptionState(sub) : 'Básico'}
              </p>
              <h2 className="my-5 text-5xl font-black">{pro ? 'PRO' : 'Básico'}</h2>
              <p className="text-sm text-zinc-300">
                {pro
                  ? 'Publicaciones de solicitudes ilimitadas.'
                  : 'Publicá solicitudes y encontrá profesionales de tu barrio.'}
              </p>
              {sub?.fechaInicio && (
                <p className="mt-5 text-sm text-zinc-400">
                  Desde el {fecha(sub.fechaInicio)}
                </p>
              )}
              {sub?.fechaFin && (
                <p className="mt-2 text-sm text-zinc-400">
                  Hasta el {fecha(sub.fechaFin)}
                </p>
              )}
            </section>
            <section className={`${panel} space-y-5`}>
              <h2 className="text-xl font-bold">Básico o PRO</h2>
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="rounded-xl border border-[#30302f] p-4">
                  <h3 className="font-bold">Básico</h3>
                  <p className="mt-3 text-sm text-zinc-400">
                    Buscá trabajadores, guardá favoritos y recibí propuestas.
                  </p>
                </div>
                <div className="rounded-xl border border-[#ffc107]/50 p-4">
                  <h3 className="font-bold text-[#ffc107]">PRO</h3>
                  <p className="mt-3 text-sm text-zinc-400">
                    Solicitudes ilimitadas, perfil empresarial y anuncios para tu negocio.
                  </p>
                </div>
              </div>
              <Note>
                El plan PRO se activa cuando se verifica tu transferencia. Registrá el
                aporte como suscripción para que pueda revisarse.
              </Note>
            </section>
          </div>
          <section className={`${panel} space-y-4`}>
            <h2 className="text-xl font-bold">Tus transferencias</h2>
            <p className="text-sm text-zinc-400">
              Registrá una transferencia realizada o consultá el estado de tus aportes.
            </p>
            <Link href="/empleador/transferencias">
              <Button>Registrar transferencia</Button>
            </Link>
          </section>
        </>
      )}
    </div>
  );
}
