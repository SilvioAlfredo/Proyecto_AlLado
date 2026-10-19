'use client';

import Link from 'next/link';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { perfilEmpresarialService } from '@/features/comunidad/services/perfilEmpresarialService';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { PageHeader, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Feedback, LoadError } from './Feedback';
import { errorMessage } from '@/lib/errors';
import { safeWebUrl } from '../lib/publicidad';

const card = 'space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5';

/**
 * Presenta el perfil empresarial de un empleador para revisión.
 *
 * La verificación registra al administrador autenticado en el backend.
 * Los perfiles verificados muestran su supervisor; no permite rechazo ni revocación.
 *
 * @param props - Opciones del detalle.
 * @param props.id - Identificador del empleador, no del perfil empresarial.
 */
export function EmpresaAdminDetail({ id }: { id: number }) {
  const { user } = useAuth();
  const cache = useQueryClient();
  const valid = Number.isSafeInteger(id) && id > 0;
  const query = useQuery({
    queryKey: ['admin-empresa', id],
    queryFn: () => perfilEmpresarialService.obtener(id),
    enabled: valid,
  });
  const mutation = useMutation({
    mutationFn: () => perfilEmpresarialService.verificar(query.data!.id!),
    onSuccess: (data) => {
      cache.setQueryData(['admin-empresa', id], data);
      void cache.invalidateQueries({ queryKey: ['admin-empresas'] });
      void cache.invalidateQueries({ queryKey: ['admin-personas'] });
    },
  });

  if (!valid) {
    return <Feedback message="Empresa inválida." />;
  }
  if (query.isPending) {
    return <p role="status">Cargando empresa…</p>;
  }
  if (query.isError) {
    return (
      <LoadError message={errorMessage(query.error)} retry={() => void query.refetch()} />
    );
  }
  const item = query.data;

  return (
    <div className="space-y-6">
      <Link href="/admin/empresas" className="text-[#ffc107]">
        ← Volver a Empresas
      </Link>
      <PageHeader
        eyebrow="Moderación"
        title="Revisar empresa"
        subtitle={item.nombreEmpresa}
      />
      <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
        <section className={card}>
          <h2 className="text-xl font-bold">{item.nombreEmpresa}</h2>
          <p className="text-[#ffc107]">
            {item.verificado ? 'Verificada' : 'Sin verificar'}
          </p>
          <p>CUIT: {item.cuit || 'Sin registrar'}</p>
          <h3 className="font-bold">Información empresarial</h3>
          <p className="whitespace-pre-wrap">{item.descripcion || 'Sin descripción'}</p>
          <p>
            Sitio web:{' '}
            {safeWebUrl(item.sitioWeb) ? (
              <a
                href={safeWebUrl(item.sitioWeb)}
                target="_blank"
                rel="noopener noreferrer"
                className="break-all text-[#ffc107]"
              >
                {item.sitioWeb}
              </a>
            ) : (
              'Sin sitio válido'
            )}
          </p>
          <p className="break-all">URL del logo: {item.urlLogo || 'Sin registrar'}</p>
          <h3 className="font-bold">Titular</h3>
          <p>{item.empleador?.nombre}</p>
          <p>{item.empleador?.email}</p>
          {item.empleador?.id && (
            <Link
              href={`/admin/personas/empleadores/${item.empleador.id}`}
              className="text-[#ffc107]"
            >
              Ver ficha del titular →
            </Link>
          )}
        </section>
        <aside className={card}>
          <h2 className="font-bold">
            {item.verificado ? 'Supervisión registrada' : 'Supervisión al verificar'}
          </h2>
          <p>
            {item.verificado
              ? item.supervisadoPor?.nombre || 'Sin supervisor registrado'
              : user?.email}
          </p>
          {item.verificado && <p>{item.supervisadoPor?.email}</p>}
          <Note>
            Verificar marca el perfil y registra al administrador actual. No hay rechazo
            ni revocación desde este panel.
          </Note>
          {!item.verificado && (
            <Button
              disabled={mutation.isPending || !item.id}
              onClick={() => {
                if (confirm(`¿Verificar ${item.nombreEmpresa}?`)) {
                  mutation.mutate();
                }
              }}
            >
              {mutation.isPending ? 'Verificando…' : 'Verificar empresa'}
            </Button>
          )}
          {mutation.isError && <Feedback message={errorMessage(mutation.error)} />}
          {mutation.isSuccess && <Feedback success message="Empresa verificada." />}
        </aside>
      </div>
    </div>
  );
}
