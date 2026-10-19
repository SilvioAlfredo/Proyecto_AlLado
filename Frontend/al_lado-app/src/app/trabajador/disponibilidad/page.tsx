'use client';

import { useState, type FormEvent } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { horariosService } from '@/features/usuario/services/perfilService';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import {
  TrabajadorPage,
  EstadoTrabajador,
  campoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import type { HorarioDisponibilidadDTO } from '@/types/usuario';

const DIAS = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'];
export default function DisponibilidadPage() {
  const { userId } = useAuth();
  const consulta = useTrabajadorQuery('horarios', (id) => horariosService.listar(id));
  const [mensaje, setMensaje] = useState('');
  const agregar = useMutation({
    mutationFn: (dto: Partial<HorarioDisponibilidadDTO>) => {
      if (!userId) throw new Error('No se pudo identificar tu usuario.');
      if (!dto.horaInicio || !dto.horaFin || dto.horaInicio >= dto.horaFin)
        throw new Error('La hora de inicio debe ser anterior a la de fin.');
      return horariosService.crear(userId, dto);
    },
    onSuccess: async () => {
      await consulta.refetch();
      setMensaje('Horario guardado.');
    },
  });
  const eliminar = useMutation({
    mutationFn: (id: number) => horariosService.eliminar(id),
    onSuccess: async () => {
      await consulta.refetch();
      setMensaje('Horario eliminado.');
    },
  });
  function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensaje('');
    const datos = new FormData(event.currentTarget);
    agregar.mutate({
      diaSemana: String(datos.get('diaSemana')),
      horaInicio: String(datos.get('horaInicio')),
      horaFin: String(datos.get('horaFin')),
    });
  }
  return (
    <TrabajadorPage titulo="Disponibilidad semanal">
      <EstadoTrabajador consulta={consulta} />
      {mensaje && <p role="status">{mensaje}</p>}
      {(agregar.error || eliminar.error) && (
        <p role="alert" className="text-red-300">
          {errorMessage(agregar.error || eliminar.error)}
        </p>
      )}
      {consulta.data && (
        <>
          <section className={`${panelTrabajador} space-y-4`}>
            {!consulta.data.length && <p>No tenés horarios registrados.</p>}
            {[...consulta.data]
              .sort(
                (a, b) =>
                  DIAS.indexOf(a.diaSemana) - DIAS.indexOf(b.diaSemana) ||
                  a.horaInicio.localeCompare(b.horaInicio),
              )
              .map((h) => (
                <div
                  key={h.id}
                  className="flex flex-wrap items-center justify-between gap-3 border-b border-allado-border pb-3"
                >
                  <p>
                    {h.diaSemana} · {h.horaInicio.slice(0, 5)} a {h.horaFin.slice(0, 5)}
                  </p>
                  {h.id && (
                    <Button
                      variant="outline"
                      disabled={agregar.isPending || eliminar.isPending}
                      onClick={() => {
                        if (window.confirm('¿Eliminar este horario?'))
                          eliminar.mutate(h.id!);
                      }}
                    >
                      Eliminar
                    </Button>
                  )}
                </div>
              ))}
          </section>
          <form onSubmit={enviar} className={`${panelTrabajador} space-y-4`}>
            <h2 className="text-lg font-bold">Agregar horario</h2>
            <label className="block">
              Día
              <select name="diaSemana" className={campoTrabajador}>
                {DIAS.map((dia) => (
                  <option key={dia}>{dia}</option>
                ))}
              </select>
            </label>
            <div className="grid gap-4 sm:grid-cols-2">
              <label>
                Desde
                <input
                  name="horaInicio"
                  type="time"
                  required
                  className={campoTrabajador}
                />
              </label>
              <label>
                Hasta
                <input name="horaFin" type="time" required className={campoTrabajador} />
              </label>
            </div>
            <Button
              type="submit"
              className="bg-allado-yellow text-black"
              disabled={agregar.isPending || eliminar.isPending}
            >
              {agregar.isPending ? 'Guardando…' : 'Guardar horario'}
            </Button>
          </form>
        </>
      )}
    </TrabajadorPage>
  );
}
