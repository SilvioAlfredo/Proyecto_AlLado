'use client';

import { useParams } from 'next/navigation';
import { TransferenciaAdminDetail } from '@/features/admin/components/TransferenciaAdminDetail';

export default function TransferenciaPage() {
  const { id } = useParams<{ id: string }>();

  return <TransferenciaAdminDetail key={id} id={Number(id)} />;
}
