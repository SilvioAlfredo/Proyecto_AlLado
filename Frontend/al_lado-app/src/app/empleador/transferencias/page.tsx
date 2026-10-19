'use client';
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { donacionesService } from '@/features/usuario/services/aportesService';
import { bankAmount, money } from '@/features/admin/lib/aportes';
import { PageHeader, Empty, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import {
  Field,
  Feedback,
  QueryStatus,
  panel,
  control,
  fecha,
} from '@/features/empleador/components/EmployerUI';
export default function TransferenciasPage() {
  const { userId, perfil } = useEmpleador();
  const [page, setPage] = useState(0);
  const query = useQuery({
    queryKey: ['empleador', userId, 'transferencias', page],
    enabled: !!userId,
    queryFn: () => donacionesService.porUsuario(userId!, page),
  });
  const [monto, setMonto] = useState('');
  const [moneda, setMoneda] = useState('ARS');
  const [tipo, setTipo] = useState('SUSCRIPCION');
  const [banco, setBanco] = useState('');
  const [asunto, setAsunto] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const [ok, setOk] = useState('');
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Transferencias"
        title="Registrá tu transferencia."
        subtitle="Informá el aporte que realizaste y consultá su verificación."
        action={
          <Link className="text-sm text-[#ffc107]" href="/empleador/plan">
            Ver mi plan →
          </Link>
        }
      />
      <div className="grid gap-5 lg:grid-cols-2">
        <form
          className={`${panel} space-y-4`}
          onSubmit={async (e) => {
            e.preventDefault();
            setBusy(true);
            setError(null);
            setOk('');
            try {
              if (!perfil.data) throw new Error('No pudimos identificar tu cuenta.');
              await donacionesService.crear({
                monto: Number(bankAmount(monto)),
                moneda,
                mecanismoPago: tipo,
                banco: banco.trim(),
                tipoTransferencia: 'TRANSFERENCIA_BANCARIA',
                asuntoTransferencia: asunto.trim(),
                usuario: {
                  id: perfil.data.id,
                  nombre: perfil.data.nombre,
                  email: perfil.data.email,
                },
              });
              setMonto('');
              setAsunto('');
              setOk('Transferencia registrada. Quedó pendiente de verificación.');
              setPage(0);
              await query.refetch();
            } catch (err) {
              setError(err);
            } finally {
              setBusy(false);
            }
          }}
        >
          <h2 className="font-bold">Datos de transferencia</h2>
          <Feedback error={error} success={ok} />
          <div className="grid grid-cols-2 gap-3">
            <Field label="Monto">
              <input
                className={control}
                required
                inputMode="decimal"
                placeholder="12000,00"
                value={monto}
                onChange={(e) => setMonto(e.target.value)}
              />
            </Field>
            <Field label="Moneda">
              <select
                className={control}
                value={moneda}
                onChange={(e) => setMoneda(e.target.value)}
              >
                <option>ARS</option>
                <option>USD</option>
              </select>
            </Field>
          </div>
          <Field label="Tipo de aporte">
            <select
              className={control}
              value={tipo}
              onChange={(e) => setTipo(e.target.value)}
            >
              <option value="SUSCRIPCION">Suscripción PRO</option>
              <option value="TRANSFERENCIA">Donación</option>
            </select>
          </Field>
          <Field label="Banco de origen">
            <input
              required
              className={control}
              value={banco}
              onChange={(e) => setBanco(e.target.value)}
            />
          </Field>
          <Field label="Asunto de la transferencia">
            <input
              required
              className={control}
              value={asunto}
              onChange={(e) => setAsunto(e.target.value)}
              placeholder="Copiá el asunto del comprobante"
            />
          </Field>
          <Note>
            Este registro informa una transferencia ya realizada. La verificación de los
            datos queda a cargo de la administración.
          </Note>
          <Button className="w-full" type="submit" disabled={busy || !perfil.data}>
            {busy ? 'Registrando…' : 'Registrar aporte pendiente'}
          </Button>
        </form>
        <section className="space-y-4">
          <h2 className="text-xl font-bold">Historial de aportes</h2>
          <QueryStatus pending={query.isPending} error={query.error} />
          {query.data?.length === 0 && (
            <Empty mensaje="Todavía no registraste transferencias." />
          )}
          {query.data?.map((d) => (
            <article key={d.id} className={`${panel} space-y-2`}>
              <div className="flex flex-wrap items-center justify-between gap-2">
                <h3 className="text-xl font-bold">{money(d.monto, d.moneda)}</h3>
                <span className="text-xs text-[#ffc107]">
                  {d.estado === 'VERIFICADO'
                    ? 'Verificado'
                    : d.estado === 'RECHAZADO'
                      ? 'Rechazado'
                      : 'Pendiente'}
                </span>
              </div>
              <p className="text-sm text-zinc-400">
                {fecha(d.fechaDonacion)} ·{' '}
                {d.mecanismoPago === 'SUSCRIPCION' ? 'Suscripción PRO' : 'Donación'}
              </p>
              <p className="text-sm text-zinc-400">
                {d.banco} · {d.asuntoTransferencia}
              </p>
            </article>
          ))}
          <div className="flex items-center gap-3">
            <Button
              variant="outline"
              disabled={page === 0 || query.isFetching}
              onClick={() => setPage(page - 1)}
            >
              Anterior
            </Button>
            <span className="text-sm">Página {page + 1}</span>
            <Button
              variant="outline"
              disabled={(query.data?.length ?? 0) < 10 || query.isFetching}
              onClick={() => setPage(page + 1)}
            >
              Siguiente
            </Button>
          </div>
        </section>
      </div>
    </div>
  );
}
