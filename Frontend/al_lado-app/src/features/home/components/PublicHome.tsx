'use client';

import { useMemo, useState } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import {
  ArrowDown,
  ArrowRight,
  BriefcaseBusiness,
  Check,
  ChevronDown,
  Hammer,
  House,
  Leaf,
  MapPin,
  Monitor,
  PaintRoller,
  Search,
  Users,
  Wrench,
  Zap,
} from 'lucide-react';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';

const categories = [
  { name: 'Electricidad', icon: Zap },
  { name: 'Plomería', icon: Wrench },
  { name: 'Pintura', icon: PaintRoller },
  { name: 'Jardinería', icon: Leaf },
  { name: 'Carpintería', icon: Hammer },
  { name: 'Tecnología', icon: Monitor },
];
const primary =
  'inline-flex items-center justify-center gap-2 rounded-full bg-allado-yellow px-6 py-3 text-sm font-bold text-black transition hover:bg-allado-yellow-hover focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-allado-yellow';
const secondary =
  'inline-flex items-center justify-center gap-2 rounded-full border border-allado-border px-6 py-3 text-sm font-semibold text-white transition hover:border-zinc-500 hover:bg-allado-card focus-visible:outline-2 focus-visible:outline-allado-yellow';
const normalize = (value: string) =>
  value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();

function NeighborhoodIllustration() {
  return (
    <div
      aria-hidden="true"
      className="relative mx-auto hidden w-full max-w-[480px] overflow-hidden lg:block rounded-[2rem] bg-[#f6c945] p-7 text-[#1e211b] sm:p-9"
    >
      <div className="flex items-center justify-between text-[10px] font-bold uppercase tracking-[0.18em]">
        <span>Hecho de personas, como tu barrio.</span>
        <span className="rounded-full border border-black/20 p-2">
          <MapPin size={17} />
        </span>
      </div>
      <p className="mt-5 max-w-none text-[2rem] font-black leading-[1.05] tracking-tight sm:text-[2.6rem]">
        Lo que sabés hacer.
        <br />
        <span className="text-black/50">A la vuelta.</span>
      </p>
      <div className="relative mt-5 h-60 sm:h-64">
        <svg className="absolute inset-0 h-full w-full" viewBox="0 0 360 270" fill="none">
          <path
            d="M-20 195H380M60-20V290M230-20V290M-20 65H380"
            stroke="#e3b431"
            strokeWidth="44"
          />
          <path
            d="M-20 195H380M60-20V290M230-20V290M-20 65H380"
            stroke="#fce6a0"
            strokeWidth="23"
          />
          <path
            d="M60 65H230V195H315"
            stroke="#292b22"
            strokeWidth="2"
            strokeDasharray="5 6"
          />
          <rect x="106" y="111" width="66" height="37" rx="8" fill="#e3b431" />
          <rect x="273" y="103" width="65" height="43" rx="8" fill="#e3b431" />
          <rect x="102" y="226" width="67" height="37" rx="8" fill="#e3b431" />
          <circle cx="230" cy="65" r="7" fill="#292b22" />
        </svg>
        <div className="absolute left-0 top-4 flex -rotate-6 items-center gap-3 rounded-2xl bg-[#f9f5e9] p-4 shadow-[0_8px_25px_#5b45051a]">
          <span className="rounded-xl bg-[#dfe7cc] p-3">
            <Wrench size={25} />
          </span>
          <div>
            <p className="text-[10px] font-bold uppercase tracking-wider text-black/50">
              Tu experiencia
            </p>
            <p className="text-sm font-bold">Tiene un lugar acá.</p>
          </div>
        </div>
        <div className="absolute right-0 top-32 rotate-6 rounded-2xl bg-[#22271e] p-5 text-[#f9f5e9] shadow-xl">
          <House size={28} className="mb-3 text-[#f6c945]" />
          <p className="text-sm font-bold">
            Alguien cerca
            <br />
            necesita tu talento.
          </p>
        </div>
        <div className="absolute bottom-2 left-0 flex max-w-[190px] items-center gap-2 rounded-2xl bg-[#f9f5e9] px-3 py-2 text-[10px] font-bold">
          <span className="h-2 w-2 shrink-0 rounded-full bg-[#63794e]" />
          Una conexión, muchas posibilidades
        </div>
      </div>
      <div className="flex items-center justify-between border-t border-black/15 pt-4 text-xs font-medium">
        <span>Oficios que mueven el barrio.</span>
        <ArrowRight size={18} />
      </div>
    </div>
  );
}

export function PublicHome() {
  const [search, setSearch] = useState('');
  const [location, setLocation] = useState('');
  const [category, setCategory] = useState('');
  const [limit, setLimit] = useState(6);
  const query = useQuery({
    queryKey: ['public-oportunidades'],
    queryFn: () => oportunidadesService.listarConFiltros(),
    staleTime: 60000,
    retry: 1,
  });
  const filtered = useMemo(
    () =>
      (query.data ?? [])
        .filter(
          (o) =>
            o.activa &&
            (!search ||
              normalize(
                [o.titulo, o.contenido, ...(o.etiquetas ?? []).map((e) => e.nombre)].join(
                  ' ',
                ),
              ).includes(normalize(search.trim()))) &&
            (!location ||
              normalize(o.ubicacion ?? '').includes(normalize(location.trim()))) &&
            (!category ||
              o.etiquetas?.some((e) => normalize(e.nombre) === normalize(category))),
        )
        .sort((a, b) => (b.fechaCreacion ?? '').localeCompare(a.fechaCreacion ?? '')),
    [query.data, search, location, category],
  );
  const scrollToJobs = () =>
    document.getElementById('oportunidades')?.scrollIntoView({
      behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches
        ? 'instant'
        : 'smooth',
      block: 'start',
    });
  return (
    <div className="space-y-20 pb-4 md:space-y-28">
      <section
        className="grid items-center gap-10 pt-4 lg:grid-cols-[1.15fr_1fr] lg:gap-16 lg:pt-10"
        aria-labelledby="public-home-title"
      >
        <div>
          <p className="mb-6 flex items-center gap-2 text-[11px] font-semibold uppercase tracking-[0.2em] text-zinc-400">
            <span className="h-1.5 w-1.5 rounded-full bg-allado-yellow" />
            Trabajo local. Conexiones reales.
          </p>
          <h1
            id="public-home-title"
            className="max-w-2xl text-[clamp(2.6rem,5.5vw,4.7rem)] font-black leading-[1.04] tracking-[-0.05em] text-white"
          >
            Tu próximo trabajo
            <br />
            puede estar en <span className="text-allado-yellow">AlLado</span>
          </h1>
          <p className="mt-6 max-w-lg text-base leading-relaxed text-zinc-400 md:text-lg">
            Conectá lo que sabés hacer con quienes lo necesitan. Encontrá oportunidades,
            compartí tu oficio y hacé crecer tu trabajo cerca de casa.
          </p>
          <div className="mt-7 flex flex-wrap gap-3">
            <Link href="/register?rol=TRABAJADOR" className={primary}>
              Quiero trabajar <ArrowRight size={17} />
            </Link>
            <Link href="/register?rol=EMPLEADOR" className={secondary}>
              Necesito contratar
            </Link>
          </div>
          <p className="mt-4 text-xs text-zinc-400">
            ¿Ya sos parte?{' '}
            <Link
              href="/login"
              className="font-semibold text-zinc-300 underline decoration-zinc-600 underline-offset-4 hover:text-allado-yellow"
            >
              Iniciá sesión
            </Link>
          </p>
          <form
            role="search"
            aria-label="Buscar oportunidades"
            onSubmit={(e) => {
              e.preventDefault();
              setLimit(6);
              scrollToJobs();
            }}
            className="mt-9 rounded-2xl border border-allado-border bg-allado-card p-3"
          >
            <div className="grid gap-3 sm:grid-cols-[1fr_1fr_auto]">
              <label className="flex min-w-0 items-center gap-2 rounded-lg px-2">
                <Search size={18} className="shrink-0 text-allado-yellow" />
                <span className="sr-only">Oficio o trabajo</span>
                <input
                  className="w-full min-w-0 bg-transparent py-2 text-sm text-white placeholder:text-zinc-400 focus:outline-none focus-visible:ring-2 focus-visible:ring-allado-yellow"
                  placeholder="¿Cuál es tu oficio?"
                  value={search}
                  onChange={(e) => {
                    setSearch(e.target.value);
                    setLimit(6);
                  }}
                />
              </label>
              <label className="flex min-w-0 items-center gap-2 rounded-lg border-t border-allado-border px-2 sm:border-l sm:border-t-0">
                <MapPin size={18} className="shrink-0 text-zinc-500" />
                <span className="sr-only">Barrio o ciudad</span>
                <input
                  className="w-full min-w-0 bg-transparent py-2 text-sm text-white placeholder:text-zinc-400 focus:outline-none focus-visible:ring-2 focus-visible:ring-allado-yellow"
                  placeholder="Barrio o ciudad"
                  value={location}
                  onChange={(e) => {
                    setLocation(e.target.value);
                    setLimit(6);
                  }}
                />
              </label>
              <button type="submit" className={`${primary} px-5`}>
                Buscar
              </button>
            </div>
          </form>
          <p className="mt-3 text-xs text-zinc-400">
            Explorá las publicaciones sin crear una cuenta.
          </p>
        </div>
        <NeighborhoodIllustration />
      </section>

      <section
        aria-labelledby="specialties-title"
        className="border-y border-allado-border py-8"
      >
        <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
          <h2 id="specialties-title" className="text-lg font-bold">
            Hay lugar para tu oficio.
          </h2>
          <a
            href="#oportunidades"
            className="flex items-center gap-2 text-xs font-semibold text-zinc-400 hover:text-allado-yellow"
          >
            Explorá las oportunidades <ArrowDown size={14} />
          </a>
        </div>
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-6">
          {categories.map((c) => (
            <button
              key={c.name}
              aria-pressed={category === c.name}
              onClick={() => {
                setCategory(category === c.name ? '' : c.name);
                setLimit(6);
                scrollToJobs();
              }}
              className={`group flex items-center gap-3 rounded-xl border p-4 text-sm transition focus-visible:outline-2 focus-visible:outline-allado-yellow ${category === c.name ? 'border-allado-yellow bg-allado-yellow/10 text-allado-yellow' : 'border-allado-border bg-allado-card/50 text-zinc-300 hover:border-allado-yellow/50'}`}
            >
              <c.icon size={20} className="shrink-0 text-allado-yellow" />
              {c.name}
            </button>
          ))}
        </div>
      </section>

      <section
        id="oportunidades"
        className="scroll-mt-24 space-y-6"
        aria-labelledby="jobs-title"
      >
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="mb-2 text-[11px] font-semibold uppercase tracking-[0.2em] text-allado-yellow">
              Tu próximo paso
            </p>
            <h2 id="jobs-title" className="text-3xl font-bold tracking-tight md:text-4xl">
              Oportunidades para empezar.
            </h2>
            <p className="mt-3 text-sm text-zinc-400">
              Trabajos y solicitudes de personas que buscan lo que vos sabés hacer.
            </p>
          </div>
          <Link
            href="/register?rol=TRABAJADOR"
            className="inline-flex items-center gap-2 text-sm font-semibold text-allado-yellow"
          >
            Crear mi perfil <ArrowRight size={16} />
          </Link>
        </div>
        {(search || location || category) && (
          <div className="flex flex-wrap items-center gap-3 text-sm text-zinc-400">
            <p aria-live="polite">
              {filtered.length} resultados {category && `en ${category}`}{' '}
              {location && `· ${location}`}
            </p>
            <button
              onClick={() => {
                setSearch('');
                setLocation('');
                setCategory('');
                setLimit(6);
              }}
              className="text-allado-yellow underline underline-offset-4"
            >
              Limpiar filtros
            </button>
          </div>
        )}
        {query.isPending ? (
          <div role="status" className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            <span className="sr-only">Buscando oportunidades…</span>
            {[1, 2, 3].map((n) => (
              <div key={n} className="h-60 animate-pulse rounded-2xl bg-allado-card" />
            ))}
          </div>
        ) : query.isError ? (
          <div
            role="alert"
            className="rounded-2xl border border-allado-border bg-allado-card p-8"
          >
            <h3 className="font-bold">No pudimos cargar las oportunidades.</h3>
            <p className="mt-2 text-sm text-zinc-400">
              Podés crear tu perfil mientras tanto o volver a intentarlo.
            </p>
            <button
              onClick={() => void query.refetch()}
              className="mt-4 text-sm font-semibold text-allado-yellow"
            >
              Reintentar →
            </button>
          </div>
        ) : !filtered.length ? (
          <div className="rounded-2xl border border-dashed border-allado-border px-6 py-12 text-center">
            <Search className="mx-auto mb-4 text-zinc-500" size={28} />
            <h3 className="text-lg font-semibold">
              {search || location || category
                ? 'Todavía no encontramos coincidencias.'
                : 'Las próximas oportunidades se van a ver acá.'}
            </h3>
            <p className="mx-auto mt-2 max-w-md text-sm leading-relaxed text-zinc-400">
              {search || location || category
                ? 'Probá con otro oficio o una zona más amplia.'
                : 'Creá tu perfil para estar listo cuando aparezca un trabajo para vos.'}
            </p>
          </div>
        ) : (
          <div className="grid items-start gap-4 md:grid-cols-2 lg:grid-cols-3">
            {filtered.slice(0, limit).map((o) => (
              <article
                key={o.id}
                className="rounded-2xl border border-allado-border bg-allado-card p-6 transition hover:border-zinc-600"
              >
                <div className="mb-5 flex items-center justify-between gap-2">
                  <span className="rounded-full bg-allado-yellow/10 px-3 py-1 text-[11px] font-semibold text-allado-yellow">
                    {o.etiquetas?.[0]?.nombre || 'Trabajo local'}
                  </span>
                  <BriefcaseBusiness size={18} className="text-zinc-500" />
                </div>
                <h3 className="text-lg font-bold leading-snug">{o.titulo}</h3>
                <p className="mt-3 flex items-center gap-1.5 text-xs text-zinc-400">
                  <MapPin size={14} />
                  {o.ubicacion || 'Ubicación a convenir'}
                </p>
                <p className="mt-4 line-clamp-2 text-sm leading-relaxed text-zinc-400">
                  {o.contenido}
                </p>
                <p className="mt-4 text-sm font-semibold text-white">
                  {o.rangoSalarial || 'Presupuesto a convenir'}
                </p>
                <details className="group mt-5 border-t border-allado-border pt-4">
                  <summary className="flex cursor-pointer list-none items-center justify-between text-sm font-semibold text-allado-yellow [&::-webkit-details-marker]:hidden">
                    Ver oportunidad{' '}
                    <ChevronDown size={16} className="transition group-open:rotate-180" />
                  </summary>
                  <div className="mt-4 space-y-4">
                    <p className="whitespace-pre-wrap break-words text-sm leading-relaxed text-zinc-300">
                      {o.contenido}
                    </p>
                    <p className="text-xs text-zinc-500">
                      {o.modalidad === 'HIBRIDO'
                        ? 'Híbrido'
                        : o.modalidad === 'REMOTO'
                          ? 'Remoto'
                          : 'Presencial'}
                      {o.fechaCreacion &&
                        ` · Publicada el ${new Date(o.fechaCreacion).toLocaleDateString('es-AR')}`}
                    </p>
                    <Link
                      className={`${primary} w-full px-3`}
                      href="/register?rol=TRABAJADOR"
                    >
                      Crear cuenta para postularme
                    </Link>
                    <p className="text-center text-xs text-zinc-500">
                      ¿Ya tenés cuenta?{' '}
                      <Link href="/login" className="text-zinc-300 underline">
                        Ingresá
                      </Link>
                    </p>
                  </div>
                </details>
              </article>
            ))}
          </div>
        )}
        {!query.isPending && !query.isError && filtered.length > limit && (
          <div className="text-center">
            <button onClick={() => setLimit(limit + 6)} className={secondary}>
              Mostrar más oportunidades <ArrowDown size={16} />
            </button>
          </div>
        )}
      </section>

      <section
        id="como-funciona"
        className="scroll-mt-24 rounded-[2rem] border border-allado-border bg-allado-card/50 px-6 py-10 md:px-10 md:py-12"
        aria-labelledby="how-title"
      >
        <div className="grid gap-10 lg:grid-cols-[0.9fr_1.5fr]">
          <div>
            <p className="text-[11px] font-semibold uppercase tracking-[0.2em] text-allado-yellow">
              Simple, de persona a persona
            </p>
            <h2
              id="how-title"
              className="mt-4 text-3xl font-bold leading-tight tracking-tight"
            >
              Menos vueltas.
              <br />
              Más conexiones.
            </h2>
            <p className="mt-4 max-w-sm text-sm leading-relaxed text-zinc-400">
              Un espacio para mostrar tu experiencia y encontrar a quienes necesitan una
              mano.
            </p>
          </div>
          <ol className="grid gap-7 sm:grid-cols-3">
            {[
              [
                '01',
                'Mostrá lo que hacés',
                'Creá tu perfil con tus oficios, experiencia y trabajos anteriores.',
              ],
              [
                '02',
                'Encontrá tu oportunidad',
                'Explorá solicitudes y enviá una propuesta para el trabajo que te interese.',
              ],
              [
                '03',
                'Hacé crecer tu camino',
                'Coordiná los detalles y construí tu reputación con cada trabajo.',
              ],
            ].map(([n, title, text]) => (
              <li key={n}>
                <span className="flex h-10 w-10 items-center justify-center rounded-full border border-allado-yellow/30 text-xs font-bold text-allado-yellow">
                  {n}
                </span>
                <h3 className="mt-5 text-base font-bold">{title}</h3>
                <p className="mt-3 text-sm leading-relaxed text-zinc-400">{text}</p>
              </li>
            ))}
          </ol>
        </div>
      </section>

      <section
        className="grid items-center gap-8 lg:grid-cols-2"
        aria-labelledby="employer-title"
      >
        <div>
          <span className="mb-5 inline-flex rounded-xl border border-allado-border bg-allado-card p-3 text-allado-yellow">
            <Users size={25} />
          </span>
          <p className="text-[11px] font-semibold uppercase tracking-[0.2em] text-zinc-400">
            Para tu casa. Para tu negocio.
          </p>
          <h2
            id="employer-title"
            className="mt-4 max-w-lg text-3xl font-bold leading-tight tracking-tight md:text-4xl"
          >
            La persona indicada
            <br />
            puede estar más cerca.
          </h2>
          <p className="mt-5 max-w-lg text-sm leading-relaxed text-zinc-400">
            Desde una reparación hasta ese proyecto pendiente. Contá qué necesitás y
            recibí propuestas de profesionales de tu zona.
          </p>
          <Link href="/register?rol=EMPLEADOR" className={`${primary} mt-6`}>
            Quiero contratar <ArrowRight size={16} />
          </Link>
        </div>
        <div className="space-y-0 rounded-2xl border border-allado-border px-6 md:px-8">
          {[
            [
              'Publicá lo que necesitás',
              'Describí el trabajo, la ubicación y los detalles que importan.',
            ],
            [
              'Conocé a quienes se postulan',
              'Revisá sus perfiles, experiencia y propuestas.',
            ],
            [
              'Elegí con más información',
              'Contactá al profesional y coordiná cómo avanzar.',
            ],
          ].map(([title, text], i) => (
            <div
              key={title}
              className="flex gap-4 border-b border-allado-border py-6 last:border-0"
            >
              <span className="mt-1 flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-allado-yellow/10 text-allado-yellow">
                <Check size={15} />
              </span>
              <div>
                <p className="text-[10px] font-semibold text-zinc-400">PASO {i + 1}</p>
                <h3 className="mt-1 font-bold">{title}</h3>
                <p className="mt-2 text-sm leading-relaxed text-zinc-400">{text}</p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="rounded-[2rem] bg-allado-yellow px-6 py-10 text-center text-black md:px-10 md:py-14">
        <p className="text-[11px] font-bold uppercase tracking-[0.2em] text-black/60">
          Tu talento hace barrio.
        </p>
        <h2 className="mx-auto mt-4 max-w-2xl text-3xl font-black tracking-tight md:text-5xl">
          Lo que viene, empieza
          <br />
          con una conexión.
        </h2>
        <p className="mx-auto mt-5 max-w-lg text-sm leading-relaxed text-black/70">
          Hay algo que sabés hacer. Hay alguien que lo necesita.
          <br className="hidden sm:block" /> Encontrémonos en AlLado.
        </p>
        <Link
          href="/register"
          className="mt-7 inline-flex items-center gap-2 rounded-full bg-black px-7 py-3.5 text-sm font-bold text-white transition hover:bg-zinc-800 focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-black"
        >
          Sumarme a AlLado <ArrowRight size={17} />
        </Link>
      </section>

      <footer className="flex flex-col gap-5 border-t border-allado-border pt-7 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <Link href="/" className="text-xl font-black">
            Al<span className="text-allado-yellow">Lado.</span>
          </Link>
          <p className="mt-1 text-xs text-zinc-400">
            Personas de tu barrio para lo que necesitás.
          </p>
        </div>
        <nav
          aria-label="Enlaces del pie de página"
          className="flex flex-wrap gap-5 text-xs text-zinc-400"
        >
          <a href="#oportunidades" className="hover:text-allado-yellow">
            Oportunidades
          </a>
          <a href="#como-funciona" className="hover:text-allado-yellow">
            Cómo funciona
          </a>
          <Link href="/login" className="hover:text-allado-yellow">
            Iniciar sesión
          </Link>
        </nav>
      </footer>
    </div>
  );
}
