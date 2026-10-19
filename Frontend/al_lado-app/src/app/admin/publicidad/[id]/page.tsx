'use client';

import { useParams } from 'next/navigation';
import { PublicidadAdminDetail } from '@/features/admin/components/PublicidadAdminDetail';

export default function Page() {
  const { id } = useParams<{ id: string }>();

  return <PublicidadAdminDetail id={Number(id)} />;
}
