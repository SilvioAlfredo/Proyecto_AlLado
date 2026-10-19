'use client';

import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { useAdminMetrics } from '@/features/admin/hooks/useAdminMetrics';
import { gestionService } from '@/features/admin/services/gestionService';
import { PageHeader, StatCard, Note, Empty } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { LoadError } from '@/features/admin/components/Feedback';
import { formatDate } from '@/lib/utils';
import { errorMessage } from '@/lib/errors';

const card = 'space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5';

const actions = [
  ['Personas', '/admin/personas', 'Trabajadores, empleadores y administración'],
  ['Comunicados', '/admin/comunicados', 'Publicaciones institucionales'],
  ['Oportunidades', '/admin/oportunidades', 'Ofertas y postulaciones'],
  ['Moderación', '/admin/reportes', 'Reportes de la comunidad'],
  ['Foro y valoraciones', '/admin/foro', 'Contenido de la comunidad'],
  ['Empresas', '/admin/empresas', 'Revisión de perfiles empresariales'],
  ['Transferencias', '/admin/donaciones', 'Aportes declarados y verificación'],
  ['Suscripciones', '/admin/suscripciones', 'Planes y vigencia'],
  ['Publicidad', '/admin/publicidad', 'Anuncios y sus fechas'],
  ['Etiquetas', '/admin/etiquetas', 'Catálogo de especialidades'],
];

export default function Page() {
  const query = useAdminMetrics();
  const reports = useQuery({
    queryKey: ['admin-dashboard-reportes'],
    queryFn: () => gestionService.reportes('', 'TODOS', 0),
  });
  const notices = useQuery({
    queryKey: ['admin-dashboard-comunicados'],
    queryFn: () => gestionService.comunicados('', '', 'recientes', 0),
  });
  const metrics = query.data;
  const recent = [
    ...(reports.data?.content || []).map((item) => ({
      key: `reporte-${item.id}`,
      title: item.motivo || 'Reporte',
      label: `Reporte · ${item.estado}`,
      date: item.fechaCreacion,
      href: `/admin/reportes/${item.id}`,
    })),
    ...(notices.data?.content || []).map((item) => ({
      key: `comunicado-${item.id}`,
      title: item.titulo,
      label: 'Comunicado',
      date: item.fechaEmision,
      href: '/admin/comunicados',
    })),
  ]
    .sort((a, b) => (b.date || '').localeCompare(a.date || ''))
    .slice(0, 5);
  const completedPercentage = metrics?.metricContratos
    ? ((100 * metrics.trabajosCompletados) / metrics.metricContratos).toFixed(1)
    : '0';

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Inicio"
        title="La comunidad, en números"
        subtitle="Resumen acumulado de AlLado."
        action={
          <Link href="/admin/personas">
            <Button>Explorar personas</Button>
          </Link>
        }
      />
      {query.isPending ? (
        <p role="status">Cargando métricas…</p>
      ) : query.isError ? (
        <LoadError
          message={errorMessage(query.error)}
          retry={() => void query.refetch()}
        />
      ) : (
        metrics && (
          <>
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
              <StatCard
                label="Trabajadores"
                value={metrics.metricTrabajadores.toLocaleString('es-AR')}
                hint="Personas con perfil de trabajador"
                highlight
              />
              <StatCard
                label="Postulaciones"
                value={metrics.metricContratos.toLocaleString('es-AR')}
                hint="Total de postulaciones registradas"
              />
              <StatCard
                label="Completadas"
                value={metrics.trabajosCompletados.toLocaleString('es-AR')}
                hint={`${completedPercentage}% del total`}
              />
              <StatCard
                label="Remuneración pretendida promedio"
                value={metrics.metricAvg}
                hint="Promedio de montos positivos declarados; no representa cobros"
              />
            </div>
            <div className="grid gap-4 sm:grid-cols-2">
              <StatCard
                label="Usuarios habilitados"
                value={String(metrics.usuariosActivos ?? 0)}
                hint="Cuentas habilitadas, no sesiones conectadas"
              />
              <StatCard
                label="Usuarios deshabilitados"
                value={String(metrics.usuariosInactivos ?? 0)}
              />
            </div>
            <div className="grid gap-6 xl:grid-cols-[3fr_2fr]">
              <section className={card}>
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <h2 className="text-xl font-bold">Las 5 etiquetas más usadas</h2>
                  <Link href="/admin/etiquetas" className="text-sm text-[#ffc107]">
                    Administrar etiquetas →
                  </Link>
                </div>
                <p className="text-sm text-zinc-400">
                  Cantidad de oportunidades por etiqueta
                </p>
                {!metrics.tendencias?.length ? (
                  <Empty mensaje="Todavía no hay oportunidades con etiquetas." />
                ) : (
                  metrics.tendencias.slice(0, 5).map((item) => (
                    <div key={item.nombre} className="space-y-2">
                      <div className="flex justify-between text-sm">
                        <span>{item.nombre}</span>
                        <span className="text-[#ffc107]">{item.cantidad}</span>
                      </div>
                      <div
                        role="meter"
                        aria-label={item.nombre}
                        aria-valuemin={0}
                        aria-valuemax={100}
                        aria-valuenow={Math.max(0, Math.min(100, item.porcentaje))}
                        aria-valuetext={
                          `${item.cantidad} oportunidades; ` +
                          `${item.porcentaje}% del total`
                        }
                        className="h-2 rounded-full bg-zinc-700"
                      >
                        <div
                          className="h-2 rounded-full bg-[#ffc107]"
                          style={{
                            width: `${Math.max(0, Math.min(100, item.porcentaje))}%`,
                          }}
                        />
                      </div>
                    </div>
                  ))
                )}
                <Note>
                  Una oportunidad puede tener varias etiquetas. Los conteos no son
                  porcentajes excluyentes.
                </Note>
              </section>
              <section className={card}>
                <h2 className="text-xl font-bold">Postulaciones por estado</h2>
                {Object.entries(metrics.postulacionesPorEstado || {}).map(
                  ([state, count]) => (
                    <div
                      key={state}
                      className="flex justify-between gap-3 border-b border-zinc-800 pb-2 text-sm"
                    >
                      <span>{state}</span>
                      <strong>{count}</strong>
                    </div>
                  ),
                )}
              </section>
            </div>
            <section className={card}>
              <h2 className="font-bold">Montos declarados</h2>
              <div className="grid gap-4 sm:grid-cols-2">
                <StatCard
                  label="Aportes y planes (estimación)"
                  value={metrics.totalIngresos}
                />
                <StatCard
                  label="Remuneración en postulaciones completadas"
                  value={metrics.dineroComunidad}
                />
              </div>
              <Note>
                La estimación suma aportes declarados y planes PRO activos a un valor de
                referencia. Las remuneraciones son montos pretendidos. Estos indicadores
                no acreditan pagos recibidos.
              </Note>
            </section>
          </>
        )
      )}
      <section className={card}>
        <h2 className="text-xl font-bold">Publicaciones y reportes recientes</h2>
        <p className="text-sm text-zinc-400">
          Ordenados por fecha de creación o emisión.
        </p>
        {reports.isPending || notices.isPending ? (
          <p role="status">Cargando actividad…</p>
        ) : reports.isError || notices.isError ? (
          <LoadError
            message={errorMessage(reports.error || notices.error)}
            retry={() => {
              void reports.refetch();
              void notices.refetch();
            }}
          />
        ) : !recent.length ? (
          <Empty mensaje="Todavía no hay publicaciones ni reportes." />
        ) : (
          recent.map((item) => (
            <Link
              key={item.key}
              href={item.href}
              className="block space-y-1 border-b border-zinc-800 pb-3"
            >
              <p className="text-xs text-[#ffc107]">
                {item.label} · {formatDate(item.date)}
              </p>
              <p className="font-semibold">{item.title}</p>
            </Link>
          ))
        )}
      </section>
      <section className="space-y-4">
        <h2 className="text-xl font-bold">Accesos de gestión</h2>
        <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
          {actions.map(([title, href, description]) => (
            <Link href={href} key={href} className={`${card} hover:border-[#ffc107]`}>
              <h3 className="font-bold">{title} →</h3>
              <p className="text-sm text-zinc-400">{description}</p>
            </Link>
          ))}
        </div>
      </section>
    </div>
  );
}
