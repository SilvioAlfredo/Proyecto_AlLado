import { OportunidadLaboralDTO } from '@/types/trabajo';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { MapPin, CalendarDays, ChevronRight } from 'lucide-react';

interface OportunidadCardProps {
  oportunidad: OportunidadLaboralDTO;
  onPostularse?: (oportunidad: OportunidadLaboralDTO) => void;
  onVerPostulantes?: (oportunidad: OportunidadLaboralDTO) => void;
}

// Card estilo mockup móvil, adaptada a grid desktop.
// Backend: OportunidadLaboral = Solicitud del diseño.
export function OportunidadCard({ oportunidad, onPostularse, onVerPostulantes }: OportunidadCardProps) {
  const postulados = oportunidad.postulaciones?.length ?? 0;

  return (
    <article className="rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-4 flex gap-4 hover:border-[#ffc107]/50 transition">
      <div className="h-20 w-20 shrink-0 rounded-xl bg-[#26262a] flex items-center justify-center text-3xl">
        🛠️
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between gap-2">
          <h3 className="font-bold text-white leading-tight truncate">{oportunidad.titulo}</h3>
          {postulados > 0 ? (
            <Badge variant="warning" className="shrink-0 rounded-lg">
              {postulados} postulados
            </Badge>
          ) : (
            <Badge variant={oportunidad.activa ? 'success' : 'default'} className="shrink-0">
              {oportunidad.activa ? 'ACTIVA' : 'CERRADA'}
            </Badge>
          )}
        </div>

        <p className="mt-1 text-sm text-zinc-400 line-clamp-2">{oportunidad.contenido}</p>

        <div className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-zinc-400">
          <span className="inline-flex items-center gap-1">
            <MapPin size={14} className="text-zinc-500" />
            {oportunidad.ubicacion || 'Sin ubicación'}
          </span>
          {oportunidad.rangoSalarial && (
            <span className="font-bold text-[#ffc107]">{oportunidad.rangoSalarial}</span>
          )}
          {oportunidad.modalidad && (
            <span className="rounded-full border border-[#3a3a3f] px-2 py-0.5">{oportunidad.modalidad}</span>
          )}
        </div>

        <div className="mt-3 flex items-center justify-between border-t border-[#2e2e33] pt-3">
          <span className="inline-flex items-center gap-1 text-xs text-zinc-500">
            <CalendarDays size={14} />
            {oportunidad.fechaCreacion
              ? new Date(oportunidad.fechaCreacion).toLocaleDateString('es-AR')
              : 'Reciente'}
          </span>
          <div className="flex items-center gap-2">
            {onVerPostulantes && (
              <button
                onClick={() => onVerPostulantes(oportunidad)}
                className="inline-flex items-center text-xs text-zinc-400 hover:text-[#ffc107]"
              >
                Ver postulantes <ChevronRight size={14} />
              </button>
            )}
            {onPostularse && oportunidad.activa && (
              <Button size="sm" onClick={() => onPostularse(oportunidad)}>
                Postularse
              </Button>
            )}
          </div>
        </div>
      </div>
    </article>
  );
}
