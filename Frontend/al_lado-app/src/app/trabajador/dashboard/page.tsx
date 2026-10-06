'use client';

import { usePostulacionesTrabajador } from '@/features/postulaciones/hooks/usePostulacionesTrabajador';
import { useEstadisticasTrabajador } from '@/features/usuario/hooks/useEstadisticasTrabajador';
import { Badge } from '@/components/ui/badge';
import { Card } from '@/components/ui/card';
import { ImpuestoWidget } from '@/features/estadisticas/components/ImpuestoWidget';
import { IncomeWidget } from '@/features/estadisticas/components/IncomeWidget';

export default function TrabajadorDashboardPage() {
  const { postulaciones, loading } = usePostulacionesTrabajador();
  const { data: estadisticas, isLoading: loadingStats } = useEstadisticasTrabajador();

  const totalPostulaciones = postulaciones.length;
  const aceptadas = postulaciones.filter((p) => p.estado === 'SELECCIONADA').length;
  const rechazadas = postulaciones.filter((p) => p.estado === 'RECHAZADA').length;
  const pendientes = postulaciones.filter((p) => p.estado === 'PENDIENTE').length;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-extrabold text-white">Panel de Trabajador</h1>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card className="p-4">
          <p className="text-sm text-zinc-500">Total Postulaciones</p>
          <p className="text-2xl font-bold text-white">{loading ? '…' : totalPostulaciones}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-zinc-500">Aceptadas</p>
          <p className="text-2xl font-bold text-emerald-400">{aceptadas}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-zinc-500">Rechazadas</p>
          <p className="text-2xl font-bold text-rose-400">{rechazadas}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-zinc-500">Pendientes</p>
          <p className="text-2xl font-bold text-[#ffc107]">{pendientes}</p>
        </Card>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <ImpuestoWidget />
        <IncomeWidget />
        {estadisticas ? (
          <Card className="p-4">
            <h3 className="mb-2 text-sm text-zinc-500">Perfil y Recomendación</h3>
            <div className="space-y-2 text-sm text-zinc-200">
              <p><span className="font-medium">Nivel: </span>{estadisticas.nivelRecomendacion}/5 ⭐</p>
              <p><span className="font-medium">Completadas: </span>{estadisticas.postulacionesCompletadas}</p>
              <p><span className="font-medium">Valoraciones: </span>{estadisticas.valoracionesCount}</p>
              <p>
                <span className="font-medium">Perfil completo: </span>
                <Badge variant={estadisticas.perfilCompleto ? 'success' : 'default'}>
                  {estadisticas.perfilCompleto ? 'Sí' : 'No'}
                </Badge>
              </p>
            </div>
          </Card>
        ) : (
          loadingStats && <p className="text-sm text-zinc-500">Cargando estadísticas…</p>
        )}
      </div>
    </div>
  );
}
