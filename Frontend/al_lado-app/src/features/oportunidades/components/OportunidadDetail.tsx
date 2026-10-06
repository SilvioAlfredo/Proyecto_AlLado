import { OportunidadLaboralDTO } from '@/types/trabajo';
import { Badge } from '@/components/ui/badge';
import { formatDate } from '@/lib/utils';

interface OportunidadDetailProps {
  oportunidad: OportunidadLaboralDTO;
}

export function OportunidadDetail({ oportunidad }: OportunidadDetailProps) {
  return (
    <div className="bg-[#1a1a1c] border border-[#2e2e33] rounded-2xl p-6 space-y-4">
      <div className="flex justify-between items-start border-b border-[#2e2e33] pb-4">
        <div>
          <h2 className="text-2xl font-extrabold text-white">{oportunidad.titulo}</h2>
          <p className="text-sm text-zinc-500 mt-1">📍 {oportunidad.ubicacion}</p>
        </div>
        <Badge variant={oportunidad.activa ? 'success' : 'default'}>
          {oportunidad.activa ? 'ACTIVA' : 'CERRADA'}
        </Badge>
      </div>

      <div className="space-y-2">
        <h3 className="text-sm font-bold text-zinc-400 uppercase">Descripción</h3>
        <p className="text-zinc-300 text-sm leading-relaxed">{oportunidad.contenido}</p>
      </div>

      <div className="grid grid-cols-2 gap-4 pt-4 border-t border-[#2e2e33] text-sm">
        <div>
          <span className="text-zinc-500 block text-xs">Rango Salarial</span>
          <span className="font-bold text-[#ffc107]">{oportunidad.rangoSalarial}</span>
        </div>
        <div>
          <span className="text-zinc-500 block text-xs">Fecha de Publicación</span>
          <span className="text-zinc-300">{oportunidad.fechaCreacion ? formatDate(oportunidad.fechaCreacion) : '-'}</span>
        </div>
      </div>
    </div>
  );
}