'use client';

import { useParams } from 'next/navigation';
import { SuscripcionAdminDetail } from '@/features/admin/components/SuscripcionAdminDetail';

export default function SuscripcionPage() {
  const { id } = useParams<{ id: string }>();

  return <SuscripcionAdminDetail key={id} id={Number(id)} />;
}
