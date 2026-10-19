'use client';

import { useParams } from 'next/navigation';
import { EmpresaAdminDetail } from '@/features/admin/components/EmpresaAdminDetail';

export default function Page() {
  const { id } = useParams<{ id: string }>();

  return <EmpresaAdminDetail id={Number(id)} />;
}
