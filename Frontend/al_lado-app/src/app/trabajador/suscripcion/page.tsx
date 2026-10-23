'use client';

import { useState, type FormEvent } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import {
  suscripcionesService,
  donacionesService,
  type DonacionDTO,
} from '@/features/usuario/services/aportesService';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import {
  useTrabajadorQuery,
  registroOpcional,
} from '@/features/usuario/hooks/useTrabajadorQuery';
import {
  TrabajadorPage,
  EstadoTrabajador,
  campoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

export default function SuscripcionAportesPage() {
  const { userId } = useAuth();
  const cache = useQueryClient();
  const [mensaje, setMensaje] = useState('');
  const [formulario, setFormulario] = useState(false);
  const consulta = useTrabajadorQuery('suscripcion', (id) =>
    registroOpcional(() => suscripcionesService.porUsuario(id)),
  );
  const aportes = useTrabajadorQuery('aportes', (id) => donacionesService.porUsuario(id));
  const sub = consulta.data;
  const vigente = !!sub?.vigente;
  const cancelar = useMutation({
    mutationFn: (id: number) => suscripcionesService.cancelar(id),
    onSuccess: async () => {
      await cache.invalidateQueries({ queryKey: ['trabajador'] });
      setMensaje('Suscripción cancelada.');
    },
  });
  const registrar = useMutation({
    mutationFn: async (dto: Partial<DonacionDTO>) => {
      if (!userId) throw new Error('No se pudo identificar tu usuario.');
      const trabajador = await trabajadorService.obtenerPorId(userId);
      return donacionesService.crear({
        ...dto,
        usuario: { id: userId, nombre: trabajador.nombre, email: trabajador.email },
      });
    },
    onSuccess: async () => {
      await aportes.refetch();
      setFormulario(false);
      setMensaje(
        'Transferencia registrada. Pendiente de verificación por el administrador.',
      );
    },
  });
  function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const datos = new FormData(event.currentTarget);
    setMensaje('');
    const monto = Number(datos.get('monto'));
    if (!Number.isFinite(monto) || monto <= 0) return;
    registrar.mutate({
      monto,
      moneda: 'ARS',
      mecanismoPago:
        datos.get('tipoTransferencia') === 'SUSCRIPCION'
          ? 'SUSCRIPCION'
          : 'TRANSFERENCIA',
      estado: 'PENDIENTE',
      banco: String(datos.get('banco')).trim(),
      tipoTransferencia: String(datos.get('tipoTransferencia')),
      asuntoTransferencia: String(datos.get('asuntoTransferencia')).trim(),
    });
  }
  return (
    <TrabajadorPage titulo="Suscripción y aportes">
      <EstadoTrabajador consulta={consulta} />
      {mensaje && <p role="status">{mensaje}</p>}
      {cancelar.error && (
        <p role="alert" className="text-red-300">
          {errorMessage(cancelar.error)}
        </p>
      )}
      {consulta.data !== undefined && (
        <section className={`${panelTrabajador} space-y-3`}>
          <h2 className="text-xl font-bold">
            {sub?.nivel ?? 'Sin suscripción registrada'}
          </h2>
          {sub && (
            <>
              <p>
                Estado:{' '}
                {vigente
                  ? 'Activa y vigente'
                  : sub.activa
                    ? 'Activa, fuera de vigencia'
                    : 'Inactiva o vencida'}
              </p>
              <p>Desde: {formatDate(sub.fechaInicio)}</p>
              <p>
                Hasta:{' '}
                {sub.fechaFin
                  ? formatDate(sub.fechaFin)
                  : 'Sin fecha de vencimiento registrada'}
              </p>
            </>
          )}
          {sub?.activa && sub.id && (
            <Button
              variant="outline"
              disabled={cancelar.isPending}
              onClick={() => {
                if (window.confirm('¿Cancelar tu suscripción?')) cancelar.mutate(sub.id!);
              }}
            >
              {cancelar.isPending ? 'Cancelando…' : 'Cancelar suscripción'}
            </Button>
          )}
        </section>
      )}
      <section className={`${panelTrabajador} space-y-4`}>
        <h2 className="text-xl font-bold">Registrar transferencia</h2>
        <p>
          Si ya realizaste una transferencia, registrá sus datos para que el administrador
          la verifique. Una transferencia de suscripción puede activar PRO luego de la
          verificación.
        </p>
        <Button
          className="bg-allado-yellow text-black"
          disabled={!userId}
          onClick={() => {
            registrar.reset();
            setFormulario(true);
          }}
        >
          Registrar aporte o suscripción
        </Button>
        {formulario && (
          <form onSubmit={enviar} className="space-y-4">
            {registrar.error && (
              <p role="alert" className="text-red-300">
                {errorMessage(registrar.error)}
              </p>
            )}
            <label className="block">
              Tipo
              <select name="tipoTransferencia" className={campoTrabajador}>
                <option value="DONACION">Aporte voluntario</option>
                <option value="SUSCRIPCION">Suscripción PRO</option>
              </select>
            </label>
            <label className="block">
              Monto transferido (ARS)
              <input
                name="monto"
                type="number"
                min="0.01"
                step="0.01"
                required
                className={campoTrabajador}
              />
            </label>
            <label className="block">
              Banco
              <input name="banco" required className={campoTrabajador} />
            </label>
            <label className="block">
              Asunto de la transferencia
              <input name="asuntoTransferencia" required className={campoTrabajador} />
            </label>
            <div className="flex gap-3">
              <Button
                type="submit"
                className="bg-allado-yellow text-black"
                disabled={registrar.isPending}
              >
                {registrar.isPending ? 'Registrando…' : 'Guardar registro'}
              </Button>
              <Button
                type="button"
                variant="outline"
                disabled={registrar.isPending}
                onClick={() => setFormulario(false)}
              >
                Cancelar
              </Button>
            </div>
          </form>
        )}
      </section>
      <section className={`${panelTrabajador} space-y-4`}>
        <h2 className="text-xl font-bold">Historial de aportes</h2>
        <EstadoTrabajador consulta={aportes} />
        {aportes.data?.length === 0 && <p>Sin transferencias registradas.</p>}
        {aportes.data?.map((d) => (
          <article key={d.id} className="border-b border-allado-border pb-3">
            <p>
              {d.monto} {d.moneda} · {d.estado}
            </p>
            <p>
              {d.tipoTransferencia} · {formatDate(d.fechaDonacion)}
            </p>
            <p>{d.asuntoTransferencia}</p>
          </article>
        ))}
      </section>
    </TrabajadorPage>
  );
}
