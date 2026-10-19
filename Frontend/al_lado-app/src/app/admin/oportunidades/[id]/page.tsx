'use client';

import { useParams } from 'next/navigation';
import { OportunidadAdminDetail } from '@/features/admin/components/OportunidadAdminDetail';

export default function OportunidadPage() {
  const { id } = useParams<{ id: string }>();

  return <OportunidadAdminDetail key={id} id={Number(id)} />;
}
