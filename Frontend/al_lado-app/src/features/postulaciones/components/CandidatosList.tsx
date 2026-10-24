'use client';

import { PostulacionDTO } from '@/types/trabajo';
import { Button } from '@/components/ui/button';
import Link from 'next/link';

interface CandidatosListProps {
  postulaciones: PostulacionDTO[];
  busy?: boolean;
  onSeleccionar: (id: number) => void;
  onRechazar: (id: number) => void;
}

export function CandidatosList({
  postulaciones,
  onSeleccionar,
  onRechazar,
  busy,
}: CandidatosListProps) {
  if (postulaciones.length === 0) {
    return (
      <p className="py-8 text-center text-sm text-zinc-500">
        No hay postulantes en este filtro.
      </p>
    );
  }

  return (
    <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
      {postulaciones.map((p) => (
        <article
          key={p.id}
          className="rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
        >
          <div className="flex items-start justify-between gap-3">
            <div className="flex items-center gap-3">
              <span className="flex h-12 w-12 items-center justify-center rounded-full bg-[#3a3a3f] font-bold text-white">
                {(p.autor?.nombre || 'A').slice(0, 2).toUpperCase()}
              </span>
              <div>
                <p className="font-bold text-white">{p.autor?.nombre || 'Postulante'}</p>
                {/* AL-SEC-006: la lista de postulantes de la propia oferta sí trae el
                    contacto; es parte de la relación laboral */}
                {p.autor?.email && (
                  <a className="text-sm text-[#ffc107]" href={`mailto:${p.autor.email}`}>
                    {p.autor.email}
                  </a>
                )}
              </div>
            </div>
          </div>

          <p className="mt-3 text-sm text-zinc-300">“{p.contenido}”</p>

          <div className="mt-4 grid grid-cols-2 gap-3">
            <div className="rounded-xl bg-[#0b0b0c] border border-[#2e2e33] p-3">
              <p className="text-[11px] tracking-wide text-zinc-500">PRESUPUESTO</p>
              <p className="font-extrabold text-white">
                $ {Number(p.remuneracionPretendida || 0).toLocaleString('es-AR')}
              </p>
            </div>
            <div className="rounded-xl bg-[#0b0b0c] border border-[#2e2e33] p-3">
              <p className="text-[11px] tracking-wide text-zinc-500">ESTADO</p>
              <p className="font-bold text-[#ffc107]">{p.estado}</p>
            </div>
          </div>

          <div className="mt-4 grid grid-cols-2 gap-3">
            <Link
              className="self-center text-sm text-[#ffc107]"
              href={`/empleador/trabajadores/${p.autor?.id}`}
            >
              Ver perfil →
            </Link>
            {p.estado === 'PENDIENTE' && p.id ? (
              <div className="grid grid-cols-2 gap-2">
                <Button
                  disabled={busy}
                  aria-label="Rechazar propuesta"
                  variant="outline"
                  onClick={() => onRechazar(p.id!)}
                >
                  X
                </Button>
                <Button disabled={busy} onClick={() => onSeleccionar(p.id!)}>
                  Aceptar
                </Button>
              </div>
            ) : (
              <Button disabled className="opacity-60">
                {p.estado}
              </Button>
            )}
          </div>
        </article>
      ))}
    </div>
  );
}
