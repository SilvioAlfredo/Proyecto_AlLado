'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { etiquetasService } from '@/features/oportunidades/services/etiquetasService';
import { errorMessage } from '@/lib/errors';
import { obtenerRefSesion } from '@/lib/usuarioRef';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/features/auth/hooks/useAuth';

export default function PublicarPage() {
  const router = useRouter();
  const { user } = useAuth();
  const etiquetas = useQuery({
    queryKey: ['etiquetas'],
    queryFn: etiquetasService.listar,
    staleTime: 5 * 60 * 1000,
    retry: 1,
  });
  const [descripcion, setDescripcion] = useState('');
  const [especialidad, setEspecialidad] = useState('');
  const [ubicacion, setUbicacion] = useState('');
  const [modalidad, setModalidad] = useState('PRESENCIAL');
  const [rango, setRango] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const especialidades = etiquetas.data ?? [];
  const sinEspecialidades = !etiquetas.isPending && !especialidades.length;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const autor = obtenerRefSesion();
      if (!autor?.id) throw new Error('No se encontró tu usuario. Iniciá sesión de nuevo.');
      if (!especialidad) throw new Error('Elegí una especialidad para publicar.');
      const titulo = descripcion.split('\n')[0]?.slice(0, 80) || `Solicitud de ${especialidad}`;
      await oportunidadesService.crear({
        titulo,
        contenido: descripcion,
        autor,
        ubicacion,
        modalidad,
        rangoSalarial: rango,
        activa: true,
        etiquetas: [{ nombre: especialidad }],
      });
      router.push('/');
    } catch (err) {
      setError(errorMessage(err, 'No se pudo publicar. Verificá sesión de Empleador.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-6xl space-y-6">
      <header>
        <h1 className="text-3xl font-extrabold text-white">Nueva solicitud</h1>
        <p className="mt-1 text-zinc-400">Contanos qué necesitás y recibí propuestas de profesionales cerca tuyo.</p>
      </header>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_340px]">
        <form onSubmit={handleSubmit} className="space-y-6 rounded-2xl border border-[#2e2e33] bg-[#141416] p-6">
          {error && <div className="rounded-xl bg-red-500/10 border border-red-500/30 p-3 text-sm text-red-400">{error}</div>}

          <div>
            <div className="mb-2 flex items-center justify-between">
              <label className="font-bold text-white">📝 ¿Qué necesitás?</label>
              <span className="text-xs text-zinc-500">{descripcion.length}/500</span>
            </div>
            <textarea
              required
              maxLength={500}
              rows={4}
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              placeholder="Describí el problema o trabajo que necesitás... Ej.: El panel LED no enciende, necesito revisarlo y repararlo."
              className="w-full rounded-2xl border border-[#3a3a3f] bg-[#26262a] p-4 text-sm text-white placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-[#ffc107]"
            />
          </div>

          <div>
            <label className="mb-2 block font-bold text-white">🔧 Especialidad</label>
            {etiquetas.isError ? (
              <div
                role="alert"
                className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-400"
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
            ) : sinEspecialidades ? (
              <p className="rounded-xl border border-dashed border-[#3a3a3f] p-3 text-sm text-zinc-400">
                Todavía no hay especialidades cargadas. Probá más tarde.
              </p>
            ) : (
              <>
                <select
                  required
                  value={especialidad}
                  onChange={(e) => setEspecialidad(e.target.value)}
                  disabled={etiquetas.isPending}
                  className="w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 py-3 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#ffc107]"
                >
                  <option value="" disabled>
                    {etiquetas.isPending ? 'Cargando especialidades…' : 'Seleccioná una especialidad'}
                  </option>
                  {especialidades.map((etiqueta) => (
                    <option key={etiqueta.id ?? etiqueta.nombre} value={etiqueta.nombre}>
                      {etiqueta.nombre}
                    </option>
                  ))}
                </select>
                <div className="mt-3 flex flex-wrap gap-2">
                  {especialidades.map((etiqueta) => (
                    <button
                      type="button"
                      key={etiqueta.id ?? etiqueta.nombre}
                      aria-pressed={especialidad === etiqueta.nombre}
                      onClick={() => setEspecialidad(etiqueta.nombre)}
                      className={`rounded-full border px-4 py-1.5 text-sm transition ${
                        especialidad === etiqueta.nombre
                          ? 'border-[#ffc107] text-[#ffc107]'
                          : 'border-[#3a3a3f] text-zinc-300 hover:border-zinc-500'
                      }`}
                    >
                      {etiqueta.nombre}
                    </button>
                  ))}
                </div>
              </>
            )}
          </div>

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <label className="mb-2 block font-bold text-white">📍 Ubicación</label>
              <input
                value={ubicacion}
                onChange={(e) => setUbicacion(e.target.value)}
                placeholder="Ingresá tu ubicación o barrio"
                className="w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 py-3 text-sm text-white placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-[#ffc107]"
              />
              <button type="button" className="mt-2 text-sm font-semibold text-[#ffc107]">
                ➤ Usar mi ubicación actual
              </button>
            </div>
            <div>
              <label className="mb-2 block font-bold text-white">Modalidad / Presupuesto</label>
              <select
                value={modalidad}
                onChange={(e) => setModalidad(e.target.value)}
                className="w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 py-3 text-sm text-white"
              >
                <option>PRESENCIAL</option>
                <option>REMOTO</option>
                <option>HIBRIDO</option>
              </select>
              <input
                value={rango}
                onChange={(e) => setRango(e.target.value)}
                placeholder="Ej: $15.000 - $20.000"
                className="mt-2 w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 py-3 text-sm text-white placeholder:text-zinc-500"
              />
            </div>
          </div>

          <div>
            <div className="mb-2 flex items-center justify-between">
              <label className="font-bold text-white">📎 Fotos (opcional)</label>
              <span className="text-xs text-zinc-500">0/5</span>
            </div>
            <div className="flex flex-col gap-3 rounded-2xl border border-dashed border-[#3a3a3f] p-4 sm:flex-row sm:items-center sm:justify-between">
              <p className="text-sm text-zinc-400">Agregá fotos del problema (opcional). Esto ayuda a recibir mejores propuestas.</p>
              <Button type="button" variant="outline" className="shrink-0 border-[#ffc107] text-[#ffc107]">+ Agregar</Button>
            </div>
          </div>

          <Button
            type="submit"
            disabled={
              loading ||
              !descripcion ||
              !especialidad ||
              etiquetas.isPending ||
              etiquetas.isError ||
              sinEspecialidades
            }
            className="w-full h-[52px] text-base"
          >
            {loading ? 'Publicando...' : '✈ Publicar solicitud'}
          </Button>
          {!user && (
            <p className="text-center text-xs text-zinc-500">
              Necesitás iniciar sesión con una cuenta de empleador para publicar esta solicitud.
            </p>
          )}
        </form>

        <aside className="h-fit space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-6 lg:sticky lg:top-20">
          <h3 className="font-bold text-white">Resumen</h3>
          <dl className="space-y-2 text-sm">
            <div className="flex justify-between"><dt className="text-zinc-500">Especialidad</dt><dd className="font-semibold text-[#ffc107]">{especialidad || 'Sin definir'}</dd></div>
            <div className="flex justify-between"><dt className="text-zinc-500">Ubicación</dt><dd className="text-white">{ubicacion || 'Sin definir'}</dd></div>
            <div className="flex justify-between"><dt className="text-zinc-500">Modalidad</dt><dd className="text-white">{modalidad}</dd></div>
          </dl>
          <div className="rounded-xl bg-[#0b0b0c] p-3 text-xs text-zinc-400">
            La publicación ayuda a que profesionales relevantes reciban tu pedido con contexto claro.
          </div>
        </aside>
      </div>
    </div>
  );
}
