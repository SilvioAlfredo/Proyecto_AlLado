'use client';

import { useState, type FormEvent } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { portafolioService } from '@/features/usuario/services/perfilService';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import {
  TrabajadorPage,
  EstadoTrabajador,
  campoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import type { PortafolioItemDTO } from '@/types/usuario';

export default function PortafolioPage() {
  const { userId } = useAuth();
  const consulta = useTrabajadorQuery('portafolio', (id) => portafolioService.listar(id));
  const [modalAbierto, setModalAbierto] = useState(false);
  const [mensaje, setMensaje] = useState('');
  const crear = useMutation({
    mutationFn: (dto: Partial<PortafolioItemDTO>) => {
      if (!userId) throw new Error('No se pudo identificar tu usuario.');
      return portafolioService.crear(userId, dto);
    },
    onSuccess: async () => {
      await consulta.refetch();
      setModalAbierto(false);
      setMensaje('Trabajo agregado al portafolio.');
    },
  });
  const eliminar = useMutation({
    mutationFn: (id: number) => portafolioService.eliminar(id),
    onSuccess: async () => {
      await consulta.refetch();
      setMensaje('Trabajo eliminado.');
    },
  });
  function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const datos = new FormData(event.currentTarget);
    crear.mutate({
      titulo: String(datos.get('titulo')).trim(),
      descripcion: String(datos.get('descripcion')).trim(),
      urlArchivo: String(datos.get('urlArchivo')).trim(),
    });
  }
  return (
    <TrabajadorPage titulo="Mi portafolio">
      <EstadoTrabajador consulta={consulta} />
      {mensaje && <p role="status">{mensaje}</p>}
      {eliminar.error && (
        <p role="alert" className="text-red-300">
          {errorMessage(eliminar.error)}
        </p>
      )}
      {consulta.data && (
        <>
          <Button
            className="bg-allado-yellow text-black"
            onClick={() => {
              crear.reset();
              setModalAbierto(true);
              setMensaje('');
            }}
          >
            Agregar trabajo
          </Button>
          {!consulta.data.length && (
            <p className={panelTrabajador}>Tu portafolio está vacío.</p>
          )}
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {consulta.data.map((item) => (
              <article key={item.id} className={panelTrabajador}>
                <h2 className="text-lg font-bold">{item.titulo}</h2>
                <p className="whitespace-pre-wrap">
                  {item.descripcion || 'Sin descripción'}
                </p>
                {item.urlArchivo && /^https?:\/\//i.test(item.urlArchivo) && (
                  <a
                    href={item.urlArchivo}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-allado-yellow underline"
                  >
                    Ver archivo
                  </a>
                )}
                {item.id && (
                  <Button
                    variant="outline"
                    className="mt-4"
                    disabled={eliminar.isPending}
                    onClick={() => {
                      if (window.confirm('¿Eliminar este trabajo del portafolio?'))
                        eliminar.mutate(item.id!);
                    }}
                  >
                    Eliminar
                  </Button>
                )}
              </article>
            ))}
          </div>
        </>
      )}
      {modalAbierto && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
          <form
            onSubmit={enviar}
            className={`${panelTrabajador} w-full max-w-lg space-y-4`}
          >
            <h2 className="text-xl font-bold">Agregar trabajo</h2>
            {crear.error && (
              <p role="alert" className="text-red-300">
                {errorMessage(crear.error)}
              </p>
            )}
            <label className="block">
              Título
              <input name="titulo" maxLength={120} required className={campoTrabajador} />
            </label>
            <label className="block">
              Descripción
              <textarea name="descripcion" rows={4} className={campoTrabajador} />
            </label>
            <label className="block">
              URL del archivo
              <input
                name="urlArchivo"
                type="url"
                placeholder="https://"
                className={campoTrabajador}
              />
            </label>
            <div className="flex gap-3">
              <Button
                type="submit"
                className="bg-allado-yellow text-black"
                disabled={crear.isPending}
              >
                {crear.isPending ? 'Guardando…' : 'Guardar'}
              </Button>
              <Button
                type="button"
                variant="outline"
                disabled={crear.isPending}
                onClick={() => setModalAbierto(false)}
              >
                Cancelar
              </Button>
            </div>
          </form>
        </div>
      )}
    </TrabajadorPage>
  );
}
