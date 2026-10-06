'use client';

import { useOportunidades } from '@/features/oportunidades/hooks/useOportunidades';
import { Button } from '@/components/ui/button';
import Link from 'next/link';

export default function EmpleadorDashboardPage() {
  const { oportunidades, loading } = useOportunidades();

  const oportunidadesActivas = oportunidades.filter(op => op.activa).length;
  const oportunidadesCerradas = oportunidades.filter(op => !op.activa).length;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-extrabold text-white">Panel de Empleador</h1>
        <Link href="/empleador/oportunidades">
          <Button className="bg-[#ffc107] text-black hover:bg-[#eab308]">+ Nueva Oferta</Button>
        </Link>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div className="bg-[#1a1a1c] p-6 rounded-2xl border border-[#2e2e33]">
          <p className="text-sm text-zinc-500">Oportunidades Activas</p>
          <p className="text-2xl font-bold text-emerald-400">{oportunidadesActivas}</p>
        </div>
        <div className="bg-[#1a1a1c] p-6 rounded-2xl border border-[#2e2e33]">
          <p className="text-sm text-zinc-500">Oportunidades Cerradas</p>
          <p className="text-2xl font-bold text-zinc-400">{oportunidadesCerradas}</p>
        </div>
      </div>

      <div className="bg-[#1a1a1c] p-6 rounded-2xl border border-[#2e2e33]">
        <h2 className="text-lg font-bold text-white mb-4">Tus Ofertas Activas</h2>
        {loading ? (
          <p className="text-zinc-500">Cargando ofertas...</p>
        ) : (
          <ul className="divide-y divide-[#2e2e33]">
            {oportunidades.map((item, index) => (
              <li key={item.id ?? index} className="py-4 flex justify-between items-center">
                <div>
                  <p className="font-medium text-white">{item.titulo}</p>
                  <p className="text-xs text-zinc-500">{item.rangoSalarial}</p>
                </div>
                <Link href={`/empleador/candidatos?oportunidadId=${item.id}`}>
                  <Button size="sm" variant="outline" className="border-[#2e2e33] text-zinc-300 hover:border-[#ffc107] hover:text-[#ffc107]">
                    Ver Candidatos
                  </Button>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
