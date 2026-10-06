'use client';

import { useState, useEffect } from 'react';
import { DashboardMetricsDTO } from '@/types/admin';
import { adminService } from '../services/adminService';

export function useAdminMetrics() {
  const [metrics, setMetrics] = useState<DashboardMetricsDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    adminService
      .getMetrics()
      .then(setMetrics)
      .catch((err) => setError(err?.response?.data?.message || 'Error al obtener métricas'))
      .finally(() => setLoading(false));
  }, []);

  return { metrics, loading, error };
}