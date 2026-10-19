'use client';
import { useParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { publicidadesService } from '@/features/comunidad/services/moderacionService';
import { AnuncioForm } from '@/features/empleador/components/AnuncioForm';
import { QueryStatus } from '@/features/empleador/components/EmployerUI';
export default function Page() {
  const { id } = useParams<{ id: string }>();
  const { userId } = useAuth();
  const query = useQuery({
    queryKey: ['empleador', userId, 'anuncios'],
    enabled: !!userId,
    queryFn: () => publicidadesService.porAnunciante(userId!),
  });
  if (query.isPending || query.error)
    return <QueryStatus pending={query.isPending} error={query.error} />;
  const item = query.data?.find((a) => a.id === Number(id));
  return item ? (
    <AnuncioForm key={id} initial={item} />
  ) : (
    <p role="alert">No encontramos este anuncio en tu cuenta.</p>
  );
}
