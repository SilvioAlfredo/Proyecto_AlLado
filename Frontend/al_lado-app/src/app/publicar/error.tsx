'use client';

export default function PublicarError({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <div className="max-w-2xl mx-auto py-12 px-4">
      <div className="space-y-6">
        <h1 className="text-2xl font-extrabold text-white">Publicar oportunidad</h1>
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">
          {error.message ?? 'No se pudo cargar el formulario de publicación.'}
        </div>
        <button
          onClick={() => reset()}
          className="inline-flex items-center gap-2 rounded-xl bg-allado-yellow px-4 py-3 text-sm font-bold text-black hover:bg-allado-yellow-hover"
        >
          Reintentar
        </button>
      </div>
    </div>
  );
}
