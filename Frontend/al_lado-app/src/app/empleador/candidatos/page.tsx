'use client';

import { Suspense, useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { postulacionesService } from '@/features/postulaciones/services/postulacionesService';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { CandidatosList } from '@/features/postulaciones/components/CandidatosList';
import { PostulacionDTO, OportunidadLaboralDTO } from '@/types/trabajo';

type Tab = 'TODOS' | 'PENDIENTE' | 'SELECCIONADA' | 'RECHAZADA';

// Pantalla "Postulantes recibidos" del mockup, adaptada a desktop.
// Backend: GET /postulaciones (filtrado cliente por oportunidadId) + PATCH seleccionar/rechazar.
function CandidatosContent() {
  const searchParams = useSearchParams();
  const oportunidadId = searchParams.get('oportunidadId');
  const [postulaciones, setPostulaciones] = useState<PostulacionDTO[]>([]);
  const [oportunidad, setOportunidad] = useState<OportunidadLaboralDTO | null>(null);
  const [tab, setTab] = useState<Tab>('TODOS');
  const [loading, setLoading] = useState(true);

  const cargar = async () => {
    setLoading(true);
    try {
      const data = await postulacionesService.obtenerTodas();
      setPostulaciones(
        oportunidadId ? data.filter((p) => p.oportunidadLaboral?.id === Number(oportunidadId)) : data
      );
      if (oportunidadId) {
        try {
          setOportunidad(await oportunidadesService.obtenerPorId(Number(oportunidadId)));
        } catch {
          setOportunidad(null);
        }
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [oportunidadId]);

  const counts = useMemo(() => ({
    TODOS: postulaciones.length,
    PENDIENTE: postulaciones.filter((p) => p.estado === 'PENDIENTE').length,
    SELECCIONADA: postulaciones.filter((p) => p.estado === 'SELECCIONADA').length,
    RECHAZADA: postulaciones.filter((p) => p.estado === 'RECHAZADA').length,
  }), [postulaciones]);

  const filtradas = tab === 'TODOS' ? postulaciones : postulaciones.filter((p) => p.estado === tab);

  return (
    <div className="mx-auto max-w-6xl space-y-6">
      <header className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
        <div>
          <h1 className="text-3xl font-extrabold text-white">Postulantes recibidos</h1>
          <p className="text-zinc-400">
            Para tu solicitud de: <span className="font-bold text-white">{oportunidad?.titulo || (oportunidadId ? `#${oportunidadId}` : 'Todas')}</span>
          </p>
        </div>
        {oportunidad && (
          <div className="flex items-center gap-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-3 text-sm">
            <span className="flex h-14 w-14 items-center justify-center rounded-xl bg-[#26262a] text-2xl">💡</span>
            <div className="text-zinc-400">
              <p>{oportunidad.ubicacion || 'Villa Urquiza, CABA'}</p>
              <p>Publicado {oportunidad.fechaCreacion ? new Date(oportunidad.fechaCreacion).toLocaleDateString('es-AR') : 'reciente'}</p>
            </div>
          </div>
        )}
      </header>

      <div className="flex gap-6 border-b border-[#2e2e33] text-sm">
        {(Object.keys(counts) as Tab[]).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`pb-2 font-semibold ${tab === t ? 'border-b-2 border-[#ffc107] text-[#ffc107]' : 'text-zinc-500 hover:text-white'}`}
          >
            {t === 'TODOS' ? `Todos (${counts.TODOS})` : t === 'PENDIENTE' ? `Pendientes (${counts.PENDIENTE})` : t === 'SELECCIONADA' ? `Seleccionados (${counts.SELECCIONADA})` : `Rechazados (${counts.RECHAZADA})`}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
          {[1, 2].map((i) => (
            <div key={i} className="h-56 animate-pulse rounded-2xl bg-[#1a1a1c]" />
          ))}
        </div>
      ) : (
        <CandidatosList
          postulaciones={filtradas}
          onSeleccionar={async (id) => { await postulacionesService.seleccionar(id); cargar(); }}
          onRechazar={async (id) => { await postulacionesService.rechazar(id); cargar(); }}
        />
      )}
      <p className="text-xs text-zinc-600">PATCH /postulaciones/{'{id}'}/seleccionar | rechazar | completar · requiere ROLE_EMPLEADOR</p>
    </div>
  );
}

export default function EmpleadorCandidatosPage() {
  return (
    <Suspense fallback={<p className="text-zinc-500">Cargando postulantes...</p>}>
      <CandidatosContent />
    </Suspense>
  );
}
