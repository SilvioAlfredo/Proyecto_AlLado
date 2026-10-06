import { useStats } from '@/features/estadisticas/hooks/useStats';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

export function IncomeWidget() {
  const { data } = useStats();

  return (
    <Card className="p-4">
      <h3 className="mb-2 text-sm text-zinc-500">Ingresos</h3>
      <div className="space-y-2 text-sm text-zinc-200">
        <p>
          <span className="font-medium">Ingresos totales: </span>
          ${data?.ingresosTotales?.toLocaleString() || '0'}
        </p>
        <p>
          <span className="font-medium">Últimos 3 meses: </span>
          ${data?.ingresosUltimos3Meses?.toLocaleString() || '0'}
        </p>
        <p>
          <span className="font-medium">Ofertas publicadas: </span>
          <Badge variant="default">{data?.ofertasPublicadas ?? 0}</Badge>
        </p>
      </div>
    </Card>
  );
}

// Alias para import roto en dashboard (IngresosWidget vs IncomeWidget)
export const IngresosWidget = IncomeWidget;
