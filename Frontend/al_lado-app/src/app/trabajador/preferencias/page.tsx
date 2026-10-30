'use client';

import { useState, type FormEvent } from 'react';
import Link from 'next/link';
import { useMutation } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { preferenciasService } from '@/features/usuario/services/perfilService';
import {
  useTrabajadorQuery,
  registroOpcional,
} from '@/features/usuario/hooks/useTrabajadorQuery';
import {
  TrabajadorPage,
  EstadoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { Note } from '@/components/ui/shared';
import type { PreferenciaNotificacionDTO } from '@/types/usuario';

export default function PreferenciasPage() {
  const { userId } = useAuth();
  const consulta = useTrabajadorQuery('preferencias', (id) =>
    registroOpcional(() => preferenciasService.obtener(id)),
  );
  return (
    <TrabajadorPage titulo="Preferencias">
      <EstadoTrabajador consulta={consulta} />
      {consulta.data !== undefined && userId && (
        <Preferencias
          key={userId}
          inicial={consulta.data}
          userId={userId}
          actualizar={() => consulta.refetch()}
        />
      )}
      <Link href="/trabajador/seguridad" className="text-allado-yellow underline">
        Cambiar contraseña
      </Link>
    </TrabajadorPage>
  );
}

function Preferencias({
  inicial,
  userId,
  actualizar,
}: {
  inicial: PreferenciaNotificacionDTO | null;
  userId: number;
  actualizar: () => Promise<unknown>;
}) {
  const [guardado, setGuardado] = useState(false);
  const guardar = useMutation({
    mutationFn: (dto: PreferenciaNotificacionDTO) =>
      preferenciasService.guardar(userId, dto),
    onSuccess: async () => {
      await actualizar();
      setGuardado(true);
    },
  });
  function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setGuardado(false);
    const datos = new FormData(event.currentTarget);
    guardar.mutate({
      notificarEmail: datos.has('notificarEmail'),
      notificarPush: datos.has('notificarPush'),
      alertasNuevasOfertas: datos.has('alertasNuevasOfertas'),
      alertasNuevasPostulaciones: datos.has('alertasNuevasPostulaciones'),
    });
  }
  return (
    <form onSubmit={enviar} className={`${panelTrabajador} space-y-5`}>
      {!inicial && (
        <p>No tenés preferencias guardadas. Elegí las opciones y guardalas.</p>
      )}
      <Note>
        Correo y push todavía no tienen envío implementado: hoy los avisos llegan
        solo dentro de la app. Guardar estas preferencias no activa entregas por
        esos canales.
      </Note>
      {[
        ['notificarEmail', 'Notificaciones por correo'],
        ['notificarPush', 'Notificaciones push'],
        ['alertasNuevasOfertas', 'Alertas de nuevas ofertas'],
        ['alertasNuevasPostulaciones', 'Alertas de nuevas postulaciones'],
      ].map(([campo, etiqueta]) => (
        <label key={campo} className="flex items-center gap-3">
          <input
            name={campo}
            type="checkbox"
            defaultChecked={inicial?.[campo as keyof PreferenciaNotificacionDTO] === true}
          />
          {etiqueta}
        </label>
      ))}
      {guardar.error && (
        <p role="alert" className="text-red-300">
          {errorMessage(guardar.error)}
        </p>
      )}
      {guardado && (
        <p role="status" className="text-emerald-300">
          Preferencias guardadas.
        </p>
      )}
      <Button
        type="submit"
        className="bg-allado-yellow text-black"
        disabled={guardar.isPending}
      >
        {guardar.isPending ? 'Guardando…' : 'Guardar preferencias'}
      </Button>
    </form>
  );
}
