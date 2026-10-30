'use client';

import { useMemo, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { useOportunidades } from '@/features/oportunidades/hooks/useOportunidades';
import { etiquetasService } from '@/features/oportunidades/services/etiquetasService';
import { OportunidadCard } from '@/features/oportunidades/components/OportunidadCard';
import { PostulacionModal } from '@/features/postulaciones/components/PostulacionModal';
import { OportunidadLaboralDTO } from '@/types/trabajo';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Input } from '@/components/ui/input';
import {
  Bell,
  Search,
  SlidersHorizontal,
  Zap,
  Wrench,
  House,
  PaintRoller,
  Leaf,
  Monitor,
  Hammer,
  Sparkles,
  HardHat,
  Flame,
  Wind,
} from 'lucide-react';

// Icono por especialidad: el catálogo es el de GET /etiquetas, así que el mapeo
// es solo decorativo y cualquier nombre desconocido cae en el icono genérico.
const ICONOS = {
  electricidad: Zap,
  plomeria: Wrench,
  hogar: House,
  pintura: PaintRoller,
  jardineria: Leaf,
  tecnologia: Monitor,
  carpinteria: Hammer,
  limpieza: Sparkles,
  albanileria: HardHat,
  gas: Flame,
  'aire acondicionado': Wind,
};

const normalizar = (valor: string) =>
  valor
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();

export default function MemberHome() {
  const router = useRouter();
  const { nombre, user } = useAuth();
  const { oportunidades, loading } = useOportunidades();
  const etiquetas = useQuery({
    queryKey: ['etiquetas'],
    queryFn: etiquetasService.listar,
    staleTime: 5 * 60 * 1000,
    retry: 1,
  });
  const [selectedOportunidad, setSelectedOportunidad] =
    useState<OportunidadLaboralDTO | null>(null);
  const [filtro, setFiltro] = useState('');
  const [categoria, setCategoria] = useState<string | null>(null);

  const nombreCorto = nombre?.trim().split(' ')[0] ?? null;
  const inicial = (nombreCorto ?? user?.email ?? '?').charAt(0).toUpperCase();

  const oportunidadesFiltradas = useMemo(() => {
    const f = filtro.toLowerCase();
    return oportunidades.filter((op) => {
      const matchTexto =
        !f ||
        op.titulo.toLowerCase().includes(f) ||
        op.contenido.toLowerCase().includes(f) ||
        op.ubicacion.toLowerCase().includes(f);
      const matchCat =
        !categoria ||
        op.titulo.toLowerCase().includes(categoria.toLowerCase()) ||
        op.contenido.toLowerCase().includes(categoria.toLowerCase()) ||
        op.etiquetas?.some((e) => normalizar(e.nombre) === normalizar(categoria));
      return matchTexto && matchCat;
    });
  }, [oportunidades, filtro, categoria]);

  return (
    <div className="space-y-8">
      {/* Header: saludo e inicial con la identidad real de la sesión */}
      <section className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-3xl md:text-4xl font-extrabold text-white">
            {nombreCorto ? `Hola, ${nombreCorto}` : 'Hola'}
          </h1>
        </div>
        <div className="flex items-center gap-3">
          <p className="hidden lg:block text-right text-sm text-zinc-400 leading-tight">
            Personas de tu barrio
            <br />
            para lo que necesitás
          </p>
          <button className="relative flex h-10 w-10 items-center justify-center rounded-full bg-[#1a1a1c] border border-[#2e2e33]">
            <Bell size={18} />
            <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-[#ffc107]" />
          </button>
          <span className="flex h-10 w-10 items-center justify-center rounded-full bg-[#3a3a3f] font-bold text-white">
            {inicial}
          </span>
        </div>
      </section>

      {/* Buscador */}
      <div className="relative">
        <Search
          size={20}
          className="absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500"
        />
        <Input
          placeholder="Buscar electricista, plomero, hogar..."
          value={filtro}
          onChange={(e) => setFiltro(e.target.value)}
          className="h-[52px] py-3.5 pl-11 pr-12 text-base"
        />
        <SlidersHorizontal
          size={20}
          className="absolute right-4 top-1/2 -translate-y-1/2 text-zinc-400"
        />
      </div>

      {/* Especialidades reales (GET /etiquetas); sin contadores hasta tener estadísticas globales */}
      <section>
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-lg font-bold text-white">Especialidades</h2>
        </div>
        {etiquetas.isPending ? (
          <div
            role="status"
            className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3"
          >
            <span className="sr-only">Cargando especialidades…</span>
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="h-[92px] animate-pulse rounded-2xl bg-[#1a1a1c]" />
            ))}
          </div>
        ) : etiquetas.isError ? (
          <div
            role="alert"
            className="flex flex-wrap items-center justify-between gap-3 rounded-2xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300"
          >
            <span>No pudimos cargar las especialidades.</span>
            <button
              type="button"
              className="font-semibold underline"
              onClick={() => void etiquetas.refetch()}
            >
              Reintentar
            </button>
          </div>
        ) : !etiquetas.data?.length ? (
          <p className="rounded-2xl border border-dashed border-[#3a3a3f] p-6 text-center text-sm text-zinc-400">
            Todavía no hay especialidades cargadas.
          </p>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
            {etiquetas.data.map((etiqueta) => {
              const Icono =
                (ICONOS as Record<string, typeof Wrench>)[normalizar(etiqueta.nombre)] ??
                Wrench;
              const activa = categoria === etiqueta.nombre;
              return (
                <button
                  key={etiqueta.id ?? etiqueta.nombre}
                  aria-pressed={activa}
                  onClick={() =>
                    setCategoria(categoria === etiqueta.nombre ? null : etiqueta.nombre)
                  }
                  className={`rounded-2xl border p-4 text-center transition ${
                    activa
                      ? 'border-[#ffc107] bg-[#ffc107]/10'
                      : 'border-[#2e2e33] bg-[#1a1a1c] hover:border-zinc-600'
                  }`}
                >
                  <Icono
                    size={28}
                    className={`mx-auto ${activa ? 'text-[#ffc107]' : 'text-zinc-300'}`}
                  />
                  <p className="mt-2 text-sm font-bold text-white">{etiqueta.nombre}</p>
                </button>
              );
            })}
          </div>
        )}
      </section>

      {/* Mis solicitudes activas — desktop: 2-3 columnas */}
      <section>
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-lg font-bold text-white">Mis solicitudes activas</h2>
          <button className="text-sm font-semibold text-[#ffc107]">Ver todas ›</button>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-40 animate-pulse rounded-2xl bg-[#1a1a1c]" />
            ))}
          </div>
        ) : oportunidadesFiltradas.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-[#3a3a3f] p-10 text-center text-zinc-500">
            No hay solicitudes. Publicá la primera con el botón “Publicar ahora”.
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
            {oportunidadesFiltradas.map((item) => (
              <OportunidadCard
                key={item.id}
                oportunidad={item}
                onPostularse={(op) => setSelectedOportunidad(op)}
                onVerPostulantes={(op) =>
                  op.id && router.push(`/empleador/candidatos?oportunidadId=${op.id}`)
                }
              />
            ))}
          </div>
        )}
      </section>

      {/* CTA Publicar */}
      <section className="flex flex-col gap-4 rounded-2xl border border-[#ffc107]/20 bg-[#1f1a08] p-6 md:flex-row md:items-center md:justify-between">
        <div className="flex items-start gap-3">
          <span className="text-3xl">🔧</span>
          <div>
            <h3 className="font-extrabold text-white">¿Necesitás otra mano?</h3>
            <p className="text-sm text-zinc-400">
              Publicá una nueva solicitud y recibí propuestas de profesionales de tu zona.
            </p>
          </div>
        </div>
        <Link
          href="/publicar"
          className="inline-flex shrink-0 items-center justify-center rounded-full bg-[#ffc107] px-6 py-3 font-bold text-black hover:bg-[#eab308]"
        >
          Publicar ahora
        </Link>
      </section>

      {/* Nav móvil estilo mockup */}
      <nav className="fixed bottom-0 left-0 right-0 z-30 border-t border-[#222] bg-[#0b0b0c] md:hidden">
        <div className="mx-auto grid max-w-md grid-cols-3 px-6 py-2 text-center text-xs">
          <Link href="/" className="font-bold text-[#ffc107]">
            🏠
            <br />
            Inicio
          </Link>
          <Link href="/publicar" className="font-bold text-zinc-400">
            ➕<br />
            Publicar
          </Link>
          <Link href="/empleador/candidatos" className="font-bold text-zinc-400">
            📄
            <br />
            Contratos
          </Link>
        </div>
      </nav>

      <PostulacionModal
        oportunidad={selectedOportunidad}
        isOpen={!!selectedOportunidad}
        onClose={() => setSelectedOportunidad(null)}
      />
    </div>
  );
}
