'use client';

import { useAdminMetrics } from '@/features/admin/hooks/useAdminMetrics';
import { KpiMetricsCard } from '@/features/admin/components/KpiMetricsCard';

export default function AdminDashboardPage() {
  const { metrics, loading } = useAdminMetrics();

  if (loading) return <p>Cargando métricas del sistema...</p>;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-extrabold text-white">Métricas Principales (Spring Boot API)</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiMetricsCard titulo="Ingresos Totales" valor={metrics?.totalIngresos ?? '$0'} icono="💰" />
        <KpiMetricsCard titulo="Postulaciones Totales" valor={metrics?.metricContratos ?? 0} icono="📜" />
        <KpiMetricsCard titulo="Trabajadores" valor={metrics?.metricTrabajadores ?? 0} icono="👥" />
        <KpiMetricsCard titulo="Trabajos Completados" valor={metrics?.trabajosCompletados ?? 0} icono="✅" />
      </div>
    </div>
  );
}
