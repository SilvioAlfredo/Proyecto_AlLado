'use client';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <div className="flex min-h-[calc(100vh-4rem)] items-center justify-center py-12">
      <div className="w-full max-w-md space-y-6">
        <div className="mb-6 flex items-center justify-center gap-2">
          <span className="flex h-10 w-10 items-center justify-center rounded-full bg-[#ffc107] font-black text-xl text-black">
            ◉
          </span>
          <span className="text-2xl font-extrabold text-white">
            Al<span className="text-[#ffc107]">L</span>ado
          </span>
        </div>
        <h1 className="text-xl font-bold text-white text-center">
          Algo salió mal
        </h1>
        <p className="text-sm text-zinc-400 text-center">
          {error.message ?? 'Ocurrió un error inesperado.'}
        </p>
        <button
          onClick={() => reset()}
          className="w-full rounded-xl bg-allado-yellow px-4 py-3 text-sm font-bold text-black hover:bg-allado-yellow-hover"
        >
          Reintentar
        </button>
      </div>
    </div>
  );
}
