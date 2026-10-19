'use client';

import { useParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { usuariosService } from '@/features/usuario/services/usuariosService';
import { PersonaDetail } from '@/features/admin/components/PersonaDetail';
import { Feedback, LoadError } from '@/features/admin/components/Feedback';
import { errorMessage } from '@/lib/errors';

export default function UsuarioDetallePage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const valid = Number.isSafeInteger(id) && id > 0;
  const query = useQuery({
    queryKey: ['admin-usuario', id],
    enabled: valid,
    retry: false,
    queryFn: () => usuariosService.obtenerPorId(id),
  });

  if (!valid) {
    return <Feedback message="El identificador no es válido." />;
  }
  if (query.isPending) {
    return <p role="status">Cargando ficha…</p>;
  }
  if (query.isError) {
    return (
      <LoadError message={errorMessage(query.error)} retry={() => void query.refetch()} />
    );
  }
  const role = query.data && 'especialidades' in query.data ? 'TRABAJADOR' : 'EMPLEADOR';

  return <PersonaDetail key={`${role}-${id}`} id={id} role={role} />;
}
