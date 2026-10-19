import { Button } from '@/components/ui/button';

/**
 * Controla la navegación usando los totales del backend.
 *
 * Recibe y devuelve páginas desde cero, pero muestra números desde uno.
 * Deshabilita la navegación durante una consulta o al alcanzar los extremos.
 */
export function Pagination({
  page,
  totalPages,
  totalElements,
  busy,
  change,
}: {
  page: number;
  totalPages: number;
  totalElements: number;
  busy: boolean;
  change: (page: number) => void;
}) {
  return (
    <nav
      aria-label="Paginación"
      className="flex flex-wrap items-center justify-between gap-3 text-sm"
    >
      <span className="text-zinc-400">
        {totalElements} registros · Página {totalPages ? page + 1 : 0} de {totalPages}
      </span>
      <div className="flex gap-2">
        <Button
          variant="outline"
          size="sm"
          disabled={busy || page === 0}
          onClick={() => change(page - 1)}
        >
          Anterior
        </Button>
        <Button
          variant="outline"
          size="sm"
          disabled={busy || page + 1 >= totalPages}
          onClick={() => change(page + 1)}
        >
          Siguiente
        </Button>
      </div>
    </nav>
  );
}
