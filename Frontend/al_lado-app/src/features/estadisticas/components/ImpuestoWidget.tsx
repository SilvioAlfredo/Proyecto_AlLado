import { useStats } from '@/features/estadisticas/hooks/useStats';
import { Card } from '@/components/ui/card';

export function ImpuestoWidget() {
  const { data } = useStats();

  return (
    <Card className="p-4">
      <h3 className="mb-2 text-sm text-zinc-500">Perfil y Recomendación</h3>
      <div className="space-y-2 text-sm text-zinc-200">
        <p>
          <span className="font-medium">Nivel de recomendación: </span>
          {data?.nivelRecomendacion ?? '-'}/5 ⭐
        </p>
        <p>
          <span className="font-medium">Perfil completo: </span>
          {data?.perfilCompleto ? 'Sí' : 'No'}
        </p>
        <p>
          <span className="font-medium">Valoraciones recibidas: </span>
          {data?.valoracionesCount ?? 0}
        </p>
      </div>
    </Card>
  );
}
