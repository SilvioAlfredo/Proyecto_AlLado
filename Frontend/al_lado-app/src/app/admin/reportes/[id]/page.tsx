'use client';

import { useParams } from 'next/navigation';
import { ReporteAdminDetail } from '@/features/admin/components/ReporteAdminDetail';

export default function ReportePage() {
  const { id } = useParams<{ id: string }>();

  return <ReporteAdminDetail key={id} id={Number(id)} />;
}
