'use client';

import { useState, type FormEvent } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import { useEstadisticasTrabajador } from '@/features/usuario/hooks/useEstadisticasTrabajador';
import {
  TrabajadorPage,
  EstadoTrabajador,
  campoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import type { TrabajadorDTO } from '@/types/usuario';

export default function MiPerfilPage() {
  const consulta = useTrabajadorQuery('perfil', (id) =>
    trabajadorService.obtenerPorId(id),
  );
  const estadisticas = useEstadisticasTrabajador();
  return (
    <TrabajadorPage titulo="Mi perfil">
      <EstadoTrabajador consulta={consulta} />
      {consulta.data && <Perfil key={consulta.data.id} trabajador={consulta.data} />}
      <EstadoTrabajador consulta={estadisticas} />
      {estadisticas.data && (
        <section className={panelTrabajador}>
          <p>
            Valoración: {estadisticas.data.promedioValoraciones}/5 (
            {estadisticas.data.valoracionesCount} valoraciones)
          </p>
          <p>Trabajos completados: {estadisticas.data.postulacionesCompletadas}</p>
          <p>
            Suscripción PRO:{' '}
            {estadisticas.data.suscripcionActiva ? 'Activa' : 'Sin suscripción activa'}
          </p>
        </section>
      )}
    </TrabajadorPage>
  );
}

function calcularCompletitud(trabajador: TrabajadorDTO) {
  const campos = [
    trabajador.nombre,
    trabajador.telefono,
    trabajador.sector,
    trabajador.biografia,
    trabajador.fotografia,
    trabajador.certificados,
  ];
  const completos = campos.filter((valor) => !!valor && String(valor).trim().length > 0)
    .length;
  const porcentaje = Math.round((completos / campos.length) * 100);
  const pendientes = [
    !trabajador.telefono && 'teléfono',
    !trabajador.sector && 'sector',
    !trabajador.biografia && 'biografía',
    !trabajador.fotografia && 'foto',
    !trabajador.certificados && 'certificados',
  ].filter(Boolean) as string[];

  return { porcentaje, pendientes };
}

function Perfil({ trabajador }: { trabajador: TrabajadorDTO }) {
  const [editando, setEditando] = useState(false);
  const [guardado, setGuardado] = useState(false);
  const cache = useQueryClient();
  const guardar = useMutation({
    mutationFn: (cambios: Partial<TrabajadorDTO>) =>
      trabajadorService.actualizarPerfil(trabajador.id, cambios),
    onSuccess: async () => {
      await cache.invalidateQueries({ queryKey: ['trabajador'] });
      setEditando(false);
      setGuardado(true);
    },
  });
  function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const campos = new FormData(event.currentTarget);
    guardar.mutate({
      nombre: String(campos.get('nombre')).trim(),
      telefono: String(campos.get('telefono')).trim(),
      sector: String(campos.get('sector')).trim(),
      biografia: String(campos.get('biografia')).trim(),
      fotografia: String(campos.get('fotografia')).trim(),
      certificados: String(campos.get('certificados')).trim(),
    });
  }
  const { porcentaje, pendientes } = calcularCompletitud(trabajador);

  return (
    <section className={panelTrabajador}>
      {guardado && (
        <p role="status" className="mb-4 text-emerald-300">
          Perfil guardado.
        </p>
      )}
      {guardar.error && (
        <p role="alert" className="text-red-300">
          {errorMessage(guardar.error)}
        </p>
      )}
      <div className="mb-5 rounded-xl border border-allado-border bg-allado-bg/80 p-4">
        <div className="flex items-center justify-between gap-3">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-zinc-400">
              Estado del perfil
            </p>
            <p className="mt-1 text-lg font-bold text-white">{porcentaje}% completo</p>
          </div>
          <span className="rounded-full bg-allado-yellow/10 px-2.5 py-1 text-xs font-bold text-allado-yellow">
            {pendientes.length ? `${pendientes.length} pendientes` : 'Listo'}
          </span>
        </div>
        {pendientes.length > 0 ? (
          <p className="mt-3 text-sm text-zinc-300">
            Siguiente paso: completar {pendientes.join(', ')}.
          </p>
        ) : (
          <p className="mt-3 text-sm text-emerald-300">
            ¡Todo listo! Tu perfil ya está completo.
          </p>
        )}
      </div>
      {!editando ? (
        <div className="space-y-4">
          <h2 className="text-xl font-bold">{trabajador.nombre}</h2>
          <p>{trabajador.sector || 'Sector sin completar'}</p>
          <p>{trabajador.email}</p>
          <p>{trabajador.telefono || 'Teléfono sin completar'}</p>
          <p className="whitespace-pre-wrap">
            {trabajador.biografia || 'Biografía sin completar'}
          </p>
          <p>
            Especialidades:{' '}
            {trabajador.especialidades?.map((e) => e.nombre).join(', ') ||
              'Sin especialidades registradas'}
          </p>
          <p>Certificados: {trabajador.certificados || 'Sin certificados registrados'}</p>
          <Button
            className="bg-allado-yellow text-black"
            onClick={() => {
              setEditando(true);
              setGuardado(false);
              guardar.reset();
            }}
          >
            Editar perfil
          </Button>
        </div>
      ) : (
        <form onSubmit={enviar} className="space-y-4">
          {[
            ['nombre', 'Nombre completo', trabajador.nombre],
            ['telefono', 'Teléfono', trabajador.telefono],
            ['sector', 'Sector', trabajador.sector],
            ['fotografia', 'URL de fotografía', trabajador.fotografia],
            ['certificados', 'Certificados', trabajador.certificados],
          ].map(([nombre, etiqueta, valor]) => (
            <label key={nombre} className="block space-y-1">
              <span>{etiqueta}</span>
              <input
                name={nombre}
                defaultValue={valor ?? ''}
                required={nombre === 'nombre' || nombre === 'telefono'}
                type={nombre === 'fotografia' ? 'url' : 'text'}
                className={campoTrabajador}
              />
            </label>
          ))}
          <label className="block space-y-1">
            <span>Biografía</span>
            <textarea
              name="biografia"
              rows={4}
              defaultValue={trabajador.biografia ?? ''}
              className={campoTrabajador}
            />
          </label>
          <div className="flex gap-3">
            <Button
              type="submit"
              className="bg-allado-yellow text-black"
              disabled={guardar.isPending}
            >
              {guardar.isPending ? 'Guardando…' : 'Guardar cambios'}
            </Button>
            <Button
              type="button"
              variant="outline"
              disabled={guardar.isPending}
              onClick={() => setEditando(false)}
            >
              Cancelar
            </Button>
          </div>
        </form>
      )}
    </section>
  );
}
