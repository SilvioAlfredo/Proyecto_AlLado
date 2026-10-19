'use client';

import { useState, type FormEvent } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { foroService } from '@/features/comunidad/services/moderacionService';
import { ReportModal } from '@/features/comunidad/components/ReportModal';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import {
  TrabajadorPage,
  EstadoTrabajador,
  campoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';
import type { PublicacionForoDTO } from '@/types/comunidad';

export default function ComunidadPage() {
  const { userId } = useAuth();
  const consulta = useTrabajadorQuery('foro', () => foroService.listar());
  const [modalPublicar, setModalPublicar] = useState(false);
  const [reporte, setReporte] = useState<PublicacionForoDTO | null>(null);
  const [mensaje, setMensaje] = useState('');
  const publicar = useMutation({
    mutationFn: async (datos: Pick<PublicacionForoDTO, 'titulo' | 'contenido'>) => {
      if (!userId) throw new Error('No se pudo identificar tu usuario.');
      const trabajador = await trabajadorService.obtenerPorId(userId);
      return foroService.crear({
        ...datos,
        autor: { id: userId, nombre: trabajador.nombre, email: trabajador.email },
      });
    },
    onSuccess: async () => {
      await consulta.refetch();
      setModalPublicar(false);
      setMensaje('Publicación creada.');
    },
  });
  function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const campos = new FormData(event.currentTarget);
    publicar.mutate({
      titulo: String(campos.get('titulo')).trim(),
      contenido: String(campos.get('contenido')).trim(),
    });
  }
  return (
    <TrabajadorPage titulo="Comunidad">
      <EstadoTrabajador consulta={consulta} />
      {mensaje && <p role="status">{mensaje}</p>}
      <Button
        className="bg-allado-yellow text-black"
        disabled={!consulta.data}
        onClick={() => {
          publicar.reset();
          setModalPublicar(true);
          setMensaje('');
        }}
      >
        Nueva publicación
      </Button>
      {consulta.data?.length === 0 && (
        <p className={panelTrabajador}>Todavía no hay publicaciones.</p>
      )}
      {[...(consulta.data ?? [])]
        .sort((a, b) => (b.fechaCreacion ?? '').localeCompare(a.fechaCreacion ?? ''))
        .map((pub) => (
          <article key={pub.id} className={`${panelTrabajador} space-y-3`}>
            <div className="flex items-center justify-between gap-3">
              <p className="text-sm text-zinc-400">
                {pub.autor?.nombre ?? 'Autor no disponible'} ·{' '}
                {formatDate(pub.fechaCreacion)}
              </p>
              <Button
                variant="outline"
                disabled={!pub.id}
                onClick={() => setReporte(pub)}
              >
                Reportar
              </Button>
            </div>
            <h2 className="text-lg font-bold">{pub.titulo}</h2>
            <p className="whitespace-pre-wrap">{pub.contenido}</p>
          </article>
        ))}
      {modalPublicar && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
          <form
            onSubmit={enviar}
            className={`${panelTrabajador} w-full max-w-lg space-y-4`}
          >
            <h2 className="text-xl font-bold">Crear publicación</h2>
            {publicar.error && (
              <p role="alert" className="text-red-300">
                {errorMessage(publicar.error)}
              </p>
            )}
            <label className="block">
              Título
              <input name="titulo" maxLength={150} required className={campoTrabajador} />
            </label>
            <label className="block">
              Contenido
              <textarea name="contenido" rows={5} required className={campoTrabajador} />
            </label>
            <div className="flex gap-3">
              <Button
                type="submit"
                className="bg-allado-yellow text-black"
                disabled={publicar.isPending}
              >
                {publicar.isPending ? 'Publicando…' : 'Publicar'}
              </Button>
              <Button
                type="button"
                variant="outline"
                disabled={publicar.isPending}
                onClick={() => setModalPublicar(false)}
              >
                Cancelar
              </Button>
            </div>
          </form>
        </div>
      )}
      <ReportModal
        key={reporte?.id ?? 'cerrado'}
        isOpen={!!reporte}
        targetId={reporte?.id ?? null}
        onClose={() => setReporte(null)}
        onSuccess={() => setMensaje('Reporte enviado al administrador.')}
      />
    </TrabajadorPage>
  );
}
