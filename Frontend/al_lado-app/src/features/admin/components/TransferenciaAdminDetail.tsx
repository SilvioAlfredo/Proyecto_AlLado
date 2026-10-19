'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { donacionesService } from '@/features/usuario/services/aportesService';
import { PageHeader, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Feedback, LoadError } from './Feedback';
import { bankAmount, money } from '@/features/admin/lib/aportes';
import { formatDate } from '@/lib/utils';
import { errorMessage } from '@/lib/errors';

/**
 * Contrasta una transferencia declarada con los datos observados en el banco.
 *
 * Exige asunto, monto y confirmación de revisión antes de solicitar la verificación.
 * No realiza cobros. Los registros ya procesados se muestran sin esa acción.
 *
 * @param props - Opciones del detalle.
 * @param props.id - Identificador de la donación o transferencia.
 */
export function TransferenciaAdminDetail({ id }: { id: number }) {
  const [asunto, setAsunto] = useState('');
  const [monto, setMonto] = useState('');
  const [confirmed, setConfirmed] = useState(false);
  const [formError, setFormError] = useState('');
  const client = useQueryClient();
  const valid = Number.isSafeInteger(id) && id > 0;
  const query = useQuery({
    queryKey: ['admin-donacion', id],
    enabled: valid,
    retry: false,
    queryFn: () => donacionesService.obtenerPorId(id),
  });
  const mutation = useMutation({
    mutationFn: (amount: string) => donacionesService.verificar(id, asunto, amount),
    onSuccess: (data) => {
      client.setQueryData(['admin-donacion', id], data);
      client.invalidateQueries({ queryKey: ['admin-donaciones'] });
      client.invalidateQueries({ queryKey: ['admin-suscripciones'] });
      client.invalidateQueries({ queryKey: ['admin-suscripcion'] });
    },
  });

  if (!valid) {
    return <Feedback message="El identificador de transferencia no es válido." />;
  }
  if (query.isPending) {
    return <p role="status">Cargando transferencia…</p>;
  }
  if (query.isError) {
    return (
      <LoadError message={errorMessage(query.error)} retry={() => void query.refetch()} />
    );
  }
  const item = query.data;
  const pending = item.estado === 'PENDIENTE';

  return (
    <div className="space-y-6">
      <Link href="/admin/donaciones" className="text-sm text-zinc-400">
        ← Volver a Transferencias
      </Link>
      <PageHeader
        eyebrow="Aportes"
        title={pending ? 'Verificar transferencia' : 'Detalle de transferencia'}
        subtitle={`Registro #${item.id}`}
      />
      {mutation.isSuccess && (
        <Feedback
          success
          message={
            item.mecanismoPago === 'SUSCRIPCION'
              ? 'Transferencia verificada. La suscripción PRO fue activada.'
              : 'Transferencia verificada correctamente.'
          }
        />
      )}
      <div className="grid gap-5 lg:grid-cols-2">
        <section className="space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
          <h2 className="font-bold">Datos almacenados</h2>
          <p className="text-xs text-[#ffc107]">{item.estado}</p>
          <p className="text-2xl font-bold">{money(item.monto, item.moneda)}</p>
          <dl className="space-y-3 text-sm">
            {[
              ['Usuario', item.usuario?.nombre],
              ['Email', item.usuario?.email],
              ['Mecanismo', item.mecanismoPago],
              ['Banco', item.banco],
              ['Tipo de transferencia', item.tipoTransferencia],
              ['Asunto declarado', item.asuntoTransferencia],
              ['Fecha del registro', formatDate(item.fechaDonacion)],
            ].map(([label, value]) => (
              <div key={label}>
                <dt className="text-xs text-zinc-500">{label}</dt>
                <dd className="break-all text-zinc-200">{value || 'No registrado'}</dd>
              </div>
            ))}
          </dl>
        </section>
        <section className="h-fit space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
          <h2 className="font-bold">
            {pending ? 'Contraste con el registro bancario' : 'Registro procesado'}
          </h2>
          {pending ? (
            <form
              className="space-y-4"
              onSubmit={(event) => {
                event.preventDefault();
                setFormError('');
                try {
                  if (!confirmed) {
                    throw new Error('Confirmá que revisaste el registro bancario.');
                  }
                  mutation.mutate(bankAmount(monto));
                } catch (error) {
                  setFormError(errorMessage(error));
                }
              }}
            >
              {formError && <Feedback message={formError} />}
              {mutation.isError && <Feedback message={errorMessage(mutation.error)} />}
              <label className="block space-y-2 text-sm">
                <span>Asunto observado</span>
                <Input
                  required
                  maxLength={100}
                  value={asunto}
                  onChange={(event) => {
                    setAsunto(event.target.value);
                    setConfirmed(false);
                  }}
                />
              </label>
              <label className="block space-y-2 text-sm">
                <span>Monto observado · {item.moneda || 'ARS'}</span>
                <Input
                  required
                  inputMode="decimal"
                  placeholder="Ejemplo: 12000.00"
                  value={monto}
                  onChange={(event) => {
                    setMonto(event.target.value);
                    setConfirmed(false);
                  }}
                />
              </label>
              <label className="flex items-start gap-2 text-sm">
                <input
                  type="checkbox"
                  required
                  checked={confirmed}
                  onChange={(event) => setConfirmed(event.target.checked)}
                />
                <span>
                  Confirmo que revisé la transferencia recibida y que estos datos
                  corresponden al registro bancario.
                </span>
              </label>
              <Note>
                El asunto y monto deben coincidir exactamente. No copies los datos
                declarados sin comprobarlos.
              </Note>
              {item.mecanismoPago === 'SUSCRIPCION' && (
                <Note>Al verificar, se activa PRO por un año desde hoy.</Note>
              )}
              <Button type="submit" disabled={mutation.isPending || !confirmed}>
                {mutation.isPending ? 'Verificando…' : 'Verificar transferencia'}
              </Button>
            </form>
          ) : (
            <Note>
              Esta transferencia ya fue procesada y no puede verificarse nuevamente.
            </Note>
          )}
          <Link
            className="inline-block text-sm text-[#ffc107]"
            href="/admin/suscripciones"
          >
            Consultar suscripciones →
          </Link>
        </section>
      </div>
    </div>
  );
}
