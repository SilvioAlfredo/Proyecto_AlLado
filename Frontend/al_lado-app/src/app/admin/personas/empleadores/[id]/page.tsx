'use client';

import { useParams } from 'next/navigation';
import { PersonaDetail } from '@/features/admin/components/PersonaDetail';

export default function PersonaPage() {
  const { id } = useParams<{ id: string }>();

  return <PersonaDetail key={id} id={Number(id)} role="EMPLEADOR" />;
}
