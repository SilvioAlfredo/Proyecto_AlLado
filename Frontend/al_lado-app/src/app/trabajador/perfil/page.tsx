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

function Perfil({ trabajador }: { trabajador: TrabajadorDTO }) {
  const [editando, setEditando] = useState(false);
  const [guardado, setGuardado] = useState(false);
  const cache = useQueryClient();
  const guardar = useMutation({
    mutationFn: (cambios: Partial<TrabajadorDTO>) =>
      trabajadorService.actualizarPerfil(trabajador.email, cambios),
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
