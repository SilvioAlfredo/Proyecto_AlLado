'use client';

import Link from 'next/link';
import { useParams } from 'next/navigation';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { postulacionesService } from '@/features/postulaciones/services/postulacionesService';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import {
  TrabajadorPage,
  EstadoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

export default function DetallePostulacionPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const cache = useQueryClient();
  const consulta = useTrabajadorQuery(`postulacion-${id}`, async (trabajadorId) => {
    if (!Number.isInteger(id) || id <= 0) throw new Error('Postulación inválida.');
    const post = await postulacionesService.obtenerPorId(id);
    if (post.autor?.id !== trabajadorId)
      throw new Error('Esta postulación no pertenece a tu usuario.');
    return post;
  });
  const retirar = useMutation({
    mutationFn: () => postulacionesService.retirar(id),
    onSuccess: async () => {
      await cache.invalidateQueries({ queryKey: ['trabajador'] });
    },
  });
  const post = consulta.data;
  const oferta = useTrabajadorQuery(
    `oferta-postulacion-${id}`,
    async () => {
      if (!post) throw new Error('No se encontró la postulación.');
      return oportunidadesService.obtenerPorId(post.oportunidadLaboral.id);
    },
    !!post && ['SELECCIONADA', 'COMPLETADA'].includes(post.estado),
  );

  return (
    <TrabajadorPage titulo="Seguimiento de postulación">
      <Link href="/trabajador/postulaciones" className="text-allado-yellow underline">
        Volver a mis postulaciones
      </Link>
      <EstadoTrabajador consulta={consulta} />
      {post && (
        <>
          <section className={`${panelTrabajador} space-y-4`}>
            <p className="font-bold">Estado: {post.estado}</p>
            <h2 className="text-xl font-bold">{post.titulo}</h2>
            <p>Cotización: ${post.remuneracionPretendida?.toLocaleString('es-AR')}</p>
            <p className="whitespace-pre-wrap">{post.contenido}</p>
            <p>Enviada: {formatDate(post.fechaCreacion)}</p>
            {retirar.error && (
              <p role="alert" className="text-red-300">
                {errorMessage(retirar.error)}
              </p>
            )}
            {post.estado === 'PENDIENTE' && (
              <Button
                variant="outline"
                disabled={retirar.isPending}
                onClick={() => {
                  if (window.confirm('¿Retirar tu postulación?')) retirar.mutate();
                }}
              >
                {retirar.isPending ? 'Retirando…' : 'Retirar postulación'}
              </Button>
            )}
          </section>
          <section className={`${panelTrabajador} space-y-3`}>
            <h2 className="text-lg font-bold">Resumen del trabajo</h2>
            <p>{post.oportunidadLaboral.titulo}</p>
            <p>{post.oportunidadLaboral.ubicacion}</p>
            <Link
              href={`/trabajador/oportunidades/${post.oportunidadLaboral.id}`}
              className="text-allado-yellow underline"
            >
              Ver oportunidad
            </Link>
            {['SELECCIONADA', 'COMPLETADA'].includes(post.estado) && (
              <>
                <EstadoTrabajador consulta={oferta} />
                {oferta.data?.autor && (
                  <div>
                    <h3 className="font-bold">Contacto del empleador</h3>
                    <p>{oferta.data.autor.nombre}</p>
                    <a
                      className="text-allado-yellow underline"
                      href={`mailto:${oferta.data.autor.email}`}
                    >
                      {oferta.data.autor.email}
                    </a>
                  </div>
                )}
              </>
            )}
          </section>
        </>
      )}
    </TrabajadorPage>
  );
}
