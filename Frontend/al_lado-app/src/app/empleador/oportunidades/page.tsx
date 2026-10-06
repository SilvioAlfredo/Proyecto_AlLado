'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useOportunidades } from '@/features/oportunidades/hooks/useOportunidades';
import { OportunidadCard } from '@/features/oportunidades/components/OportunidadCard';
import { OportunidadFormModal } from '@/features/oportunidades/components/OportunidadFormModal';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

export default function EmpleadorOportunidadesPage() {
  const router = useRouter();
  const { oportunidades, loading, refetch, cargarConFiltros, filtro, setFiltro, modalidad, setModalidad } = useOportunidades();
  const [modalAbrir, setModalAbrir] = useState(false);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-extrabold text-white">Mis Ofertas Laborales</h1>
          <p className="text-sm text-zinc-500">Gestiona tus publicaciones activas e inactivas.</p>
        </div>
        <Button onClick={() => setModalAbrir(true)}>+ Nueva Oferta</Button>
      </div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
        <Input
          placeholder="Buscar título/descripción/ubicación..."
          value={filtro || ''}
          onChange={(e) => setFiltro(e.target.value)}
        />
        <select
          value={modalidad || ''}
          onChange={(e) => setModalidad(e.target.value)}
          className="h-11 rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 text-sm text-white"
        >
          <option value="">Todas las modalidades</option>
          <option value="PRESENCIAL">Presencial</option>
          <option value="HIBRIDO">Híbrido</option>
          <option value="REMOTO">Remoto</option>
        </select>
        <Button variant="outline" onClick={() => cargarConFiltros({ filtro, modalidad })}>
          Aplicar filtros
        </Button>
      </div>

      {loading ? (
        <p className="text-zinc-500">Cargando ofertas...</p>
      ) : (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
          {oportunidades.map((item) => (
            <OportunidadCard
              key={item.id}
              oportunidad={item}
              onVerPostulantes={(op) => op.id && router.push(`/empleador/candidatos?oportunidadId=${op.id}`)}
            />
          ))}
        </div>
      )}

      <OportunidadFormModal isOpen={modalAbrir} onClose={() => setModalAbrir(false)} onSuccess={() => refetch()} />
    </div>
  );
}
