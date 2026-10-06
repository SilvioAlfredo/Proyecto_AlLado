'use client';

import { useState } from 'react';
import { useOportunidades } from '@/features/oportunidades/hooks/useOportunidades';
import { OportunidadCard } from '@/features/oportunidades/components/OportunidadCard';
import { PostulacionModal } from '@/features/postulaciones/components/PostulacionModal';
import { OportunidadLaboralDTO } from '@/types/trabajo';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

export default function TrabajadorOportunidadesPage() {
  const { oportunidades, loading, cargarConFiltros, filtro, setFiltro, modalidad, setModalidad } = useOportunidades();
  const [selected, setSelected] = useState<OportunidadLaboralDTO | null>(null);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-extrabold text-white">Oportunidades Disponibles</h1>
        <p className="text-sm text-zinc-500">Explora los trabajos publicados en la comunidad.</p>
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
        <p className="text-zinc-500">Cargando oportunidades...</p>
      ) : (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
          {oportunidades.map((item) => (
            <OportunidadCard key={item.id} oportunidad={item} onPostularse={setSelected} />
          ))}
        </div>
      )}

      <PostulacionModal oportunidad={selected} isOpen={!!selected} onClose={() => setSelected(null)} />
    </div>
  );
}
