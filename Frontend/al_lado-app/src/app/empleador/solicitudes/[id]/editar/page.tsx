'use client';
import { useParams } from 'next/navigation';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { SolicitudForm } from '@/features/empleador/components/SolicitudForm';
import { QueryStatus } from '@/features/empleador/components/EmployerUI';
export default function Page() {
  const { id } = useParams<{ id: string }>();
  const { solicitudes } = useEmpleador();
  if (solicitudes.isPending || solicitudes.error)
    return <QueryStatus pending={solicitudes.isPending} error={solicitudes.error} />;
  const item = solicitudes.data?.find((o) => o.id === Number(id));
  return item ? (
    <SolicitudForm key={id} initial={item} />
  ) : (
    <p role="alert">No encontramos esta solicitud en tu cuenta.</p>
  );
}
