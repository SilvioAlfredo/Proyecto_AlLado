'use client';

import { useMemo, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useOportunidades } from '@/features/oportunidades/hooks/useOportunidades';
import { OportunidadCard } from '@/features/oportunidades/components/OportunidadCard';
import { PostulacionModal } from '@/features/postulaciones/components/PostulacionModal';
import { OportunidadLaboralDTO } from '@/types/trabajo';
import { Input } from '@/components/ui/input';
import { Bell, Search, SlidersHorizontal, MapPin, Zap, Wrench, Home, PaintRoller, Leaf } from 'lucide-react';

const CATEGORIAS = [
  { nombre: 'Electricidad', activos: 12, icon: Zap, highlight: true },
  { nombre: 'Plomería', activos: 8, icon: Wrench, highlight: false },
  { nombre: 'Hogar', activos: 15, icon: Home, highlight: false },
  { nombre: 'Pintura', activos: 6, icon: PaintRoller, highlight: false },
  { nombre: 'Jardinería', activos: 9, icon: Leaf, highlight: false },
  { nombre: 'Tecnología', activos: 7, icon: Zap, highlight: false },
];

export default function HomePage() {
  const router = useRouter();
  const { oportunidades, loading } = useOportunidades();
  const [selectedOportunidad, setSelectedOportunidad] = useState<OportunidadLaboralDTO | null>(null);
  const [filtro, setFiltro] = useState('');
  const [categoria, setCategoria] = useState<string | null>(null);

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
        op.etiquetas?.some((e) => e.nombre.toLowerCase() === categoria.toLowerCase());
      return matchTexto && matchCat;
    });
  }, [oportunidades, filtro, categoria]);

  return (
    <div className="space-y-8">
      {/* Header estilo mockup: Hola Usuario + ubicación */}
      <section className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-3xl md:text-4xl font-extrabold text-white">Hola, Usuario</h1>
          <p className="mt-1 flex items-center gap-1 text-sm text-zinc-400">
            <MapPin size={16} className="text-[#ffc107]" />
            Villa Urquiza, CABA <span className="text-zinc-600">›</span>
          </p>
        </div>
        <div className="flex items-center gap-3">
          <p className="hidden lg:block text-right text-sm text-zinc-400 leading-tight">
            Personas de tu barrio<br />para lo que necesitás
          </p>
          <button className="relative flex h-10 w-10 items-center justify-center rounded-full bg-[#1a1a1c] border border-[#2e2e33]">
            <Bell size={18} />
            <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-[#ffc107]" />
          </button>
          <span className="flex h-10 w-10 items-center justify-center rounded-full bg-[#3a3a3f] font-bold text-white">
            U
          </span>
        </div>
      </section>

      {/* Buscador */}
      <div className="relative">
        <Search size={20} className="absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500" />
        <Input
          placeholder="Buscar electricista, plomero, hogar..."
          value={filtro}
          onChange={(e) => setFiltro(e.target.value)}
          className="h-[52px] py-3.5 pl-11 pr-12 text-base"
        />
        <SlidersHorizontal size={20} className="absolute right-4 top-1/2 -translate-y-1/2 text-zinc-400" />
      </div>

      {/* Categorías populares — desktop: 6 columnas */}
      <section>
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-lg font-bold text-white">Categorías populares</h2>
          <button className="text-sm font-semibold text-[#ffc107]">Ver todas ›</button>
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
          {CATEGORIAS.map((c) => (
            <button
              key={c.nombre}
              onClick={() => setCategoria(categoria === c.nombre ? null : c.nombre)}
              className={`rounded-2xl border p-4 text-center transition ${
                categoria === c.nombre || c.highlight
                  ? 'border-[#ffc107] bg-[#ffc107]/10'
                  : 'border-[#2e2e33] bg-[#1a1a1c] hover:border-zinc-600'
              }`}
            >
              <c.icon size={28} className={`mx-auto ${categoria === c.nombre || c.highlight ? 'text-[#ffc107]' : 'text-zinc-300'}`} />
              <p className="mt-2 text-sm font-bold text-white">{c.nombre}</p>
              <p className="text-xs text-zinc-500">{c.activos} activos</p>
            </button>
          ))}
        </div>
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
                onVerPostulantes={(op) => op.id && router.push(`/empleador/candidatos?oportunidadId=${op.id}`)}
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
          <Link href="/" className="font-bold text-[#ffc107]">🏠<br />Inicio</Link>
          <Link href="/publicar" className="font-bold text-zinc-400">➕<br />Publicar</Link>
          <Link href="/empleador/candidatos" className="font-bold text-zinc-400">📄<br />Contratos</Link>
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
