import Link from 'next/link';

// Se muestra cuando el backend rechaza por falta de plan PRO.
export function ProAviso({ mensaje, planHref }: { mensaje: string; planHref: string }) {
  const esPro = mensaje.toLowerCase().includes('pro');
  return (
    <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-400">
      <p>{mensaje}</p>
      {esPro && (
        <Link href={planHref} className="mt-1 inline-block font-bold text-[#ffc107] hover:underline">
          Ver mi plan →
        </Link>
      )}
    </div>
  );
}
