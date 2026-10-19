'use client';

import { useParams, useRouter } from 'next/navigation';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import { EstadoTrabajador } from '@/features/usuario/components/TrabajadorPage';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { PostulacionModal } from '@/features/postulaciones/components/PostulacionModal';

export default function PostularPage() {
  const { id: parametro } = useParams<{ id: string }>();
  const id = Number(parametro);
  const router = useRouter();
  const consulta = useTrabajadorQuery(`oferta-postular-${id}`, () => {
    if (!Number.isInteger(id) || id <= 0) throw new Error('Oportunidad inválida.');
    return oportunidadesService.obtenerPorId(id);
  });
  if (!consulta.data) return <EstadoTrabajador consulta={consulta} />;
  if (!consulta.data.activa) return <p>Esta oportunidad está cerrada.</p>;
  return (
    <PostulacionModal
      key={id}
      oportunidad={consulta.data}
      isOpen
      onClose={() => router.replace(`/trabajador/oportunidades/${id}`)}
      onSuccess={() => router.replace('/trabajador/postulaciones')}
    />
  );
}
