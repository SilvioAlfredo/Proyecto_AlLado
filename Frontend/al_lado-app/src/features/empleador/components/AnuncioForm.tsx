'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useEmpleador } from '../hooks/useEmpleador';
import { publicidadesService } from '@/features/comunidad/services/moderacionService';
import type { PublicidadDTO } from '@/types/comunidad';
import { Field, Feedback, control, panel, fecha } from './EmployerUI';
import { PageHeader, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
export function AnuncioForm({ initial }: { initial?: PublicidadDTO }) {
  const { perfil, refresh } = useEmpleador();
  const router = useRouter();
  const [form, setForm] = useState({
    titulo: initial?.titulo ?? '',
    contenido: initial?.contenido ?? '',
    urlImagen: initial?.urlImagen ?? '',
    urlDestino: initial?.urlDestino ?? '',
    fechaInicio: initial?.fechaInicio ?? '',
    fechaFin: initial?.fechaFin ?? '',
    activa: initial?.activa ?? true,
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<unknown>(null);
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Mis anuncios"
        title={initial ? 'Editar anuncio' : 'Crear anuncio'}
        subtitle="Dale visibilidad a tu negocio en AlLado."
        action={
          <Link href="/empleador/anuncios" className="text-sm text-[#ffc107]">
            Volver a mis anuncios →
          </Link>
        }
      />
      <div className="grid gap-5 lg:grid-cols-[1fr_300px]">
        <form
          className={`${panel} space-y-4`}
          onSubmit={async (e) => {
            e.preventDefault();
            setBusy(true);
            setError(null);
            try {
              if (!perfil.data) throw new Error('No pudimos identificar tu cuenta.');
              if (form.fechaFin < form.fechaInicio)
                throw new Error(
                  'La fecha de fin debe ser igual o posterior a la de inicio.',
                );
              const dto = {
                ...initial,
                ...form,
                titulo: form.titulo.trim(),
                anunciante: {
                  id: perfil.data.id,
                  nombre: perfil.data.nombre,
                  email: perfil.data.email,
                },
              };
              if (initial?.id) await publicidadesService.actualizar(initial.id, dto);
              else await publicidadesService.crear(dto);
              await refresh();
              router.push('/empleador/anuncios');
            } catch (err) {
              setError(err);
            } finally {
              setBusy(false);
            }
          }}
        >
          <Feedback error={error} />
          <h2 className="font-bold">Contenido del anuncio</h2>
          <Field label="Título">
            <input
              className={control}
              required
              maxLength={120}
              value={form.titulo}
              onChange={(e) => setForm({ ...form, titulo: e.target.value })}
            />
          </Field>
          <Field label="Descripción">
            <textarea
              className={control}
              rows={5}
              value={form.contenido}
              onChange={(e) => setForm({ ...form, contenido: e.target.value })}
            />
          </Field>
          <Field label="URL de imagen (opcional)">
            <input
              type="url"
              className={control}
              value={form.urlImagen}
              onChange={(e) => setForm({ ...form, urlImagen: e.target.value })}
            />
          </Field>
          <Field label="URL de destino (opcional)">
            <input
              type="url"
              className={control}
              value={form.urlDestino}
              onChange={(e) => setForm({ ...form, urlDestino: e.target.value })}
            />
          </Field>
          <div className="grid grid-cols-2 gap-3">
            <Field label="Fecha de inicio">
              <input
                type="date"
                required
                className={control}
                value={form.fechaInicio}
                onChange={(e) => setForm({ ...form, fechaInicio: e.target.value })}
              />
            </Field>
            <Field label="Fecha de fin">
              <input
                type="date"
                required
                min={form.fechaInicio}
                className={control}
                value={form.fechaFin}
                onChange={(e) => setForm({ ...form, fechaFin: e.target.value })}
              />
            </Field>
          </div>
          {initial && (
            <label className="flex items-center gap-3 text-sm">
              <input
                type="checkbox"
                checked={form.activa}
                onChange={(e) => setForm({ ...form, activa: e.target.checked })}
              />
              Anuncio activo
            </label>
          )}
          <div className="flex justify-end gap-3">
            <Link href="/empleador/anuncios">
              <Button type="button" variant="outline">
                Cancelar
              </Button>
            </Link>
            <Button type="submit" disabled={busy || !perfil.data}>
              {busy ? 'Guardando…' : 'Guardar anuncio'}
            </Button>
          </div>
        </form>
        <aside className="space-y-4">
          <section className={`${panel} space-y-3`}>
            <h2 className="font-bold">Vista previa del anuncio</h2>
            {/^https?:\/\//.test(form.urlImagen) && (
              <div
                role="img"
                aria-label="Imagen del anuncio"
                className="h-40 rounded-xl bg-cover bg-center"
                style={{ backgroundImage: `url(${JSON.stringify(form.urlImagen)})` }}
              />
            )}
            <h3 className="text-xl font-bold">{form.titulo || 'Título de tu anuncio'}</h3>
            <p className="whitespace-pre-wrap break-words text-sm text-zinc-400">
              {form.contenido || 'La descripción de tu negocio se verá acá.'}
            </p>
            <p className="text-xs text-zinc-500">
              {fecha(form.fechaInicio)} — {fecha(form.fechaFin)}
            </p>
            <p className="text-xs text-[#ffc107]">
              {form.activa ? 'Activo durante las fechas elegidas' : 'Inactivo'}
            </p>
          </section>
          <Note>
            La publicación de anuncios requiere un plan PRO activo.{' '}
            <Link className="text-[#ffc107]" href="/empleador/plan">
              Ver mi plan →
            </Link>
          </Note>
        </aside>
      </div>
    </div>
  );
}
