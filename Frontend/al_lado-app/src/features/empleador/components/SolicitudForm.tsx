'use client';
import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { useEmpleador } from '../hooks/useEmpleador';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { etiquetasService } from '@/features/oportunidades/services/etiquetasService';
import { PageHeader } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Field, Feedback, control, panel, QueryStatus } from './EmployerUI';
import type { OportunidadLaboralDTO } from '@/types/trabajo';

export function SolicitudForm({ initial }: { initial?: OportunidadLaboralDTO }) {
  const { userId, perfil, refresh } = useEmpleador();
  const router = useRouter();
  const [form, setForm] = useState({
    titulo: initial?.titulo ?? '',
    contenido: initial?.contenido ?? '',
    ubicacion: initial?.ubicacion ?? '',
    modalidad: initial?.modalidad ?? 'PRESENCIAL',
    rangoSalarial: initial?.rangoSalarial ?? '',
    especialidad: initial?.etiquetas?.[0]?.nombre ?? '',
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const tags = useQuery({ queryKey: ['etiquetas'], queryFn: etiquetasService.listar });
  function update(key: keyof typeof form, value: string) {
    setForm({ ...form, [key]: value });
  }
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Tus publicaciones"
        title={initial ? 'Editar solicitud' : 'Contanos qué necesitás.'}
        subtitle="Describí el trabajo para recibir propuestas de profesionales."
        action={
          <Link href="/empleador/solicitudes" className="text-sm text-zinc-400">
            Volver a mis solicitudes →
          </Link>
        }
      />
      <div className="grid gap-5 lg:grid-cols-[1fr_280px]">
        <form
          className={`${panel} space-y-5`}
          onSubmit={async (e) => {
            e.preventDefault();
            setBusy(true);
            setError(null);
            try {
              if (!userId || !perfil.data)
                throw new Error('No pudimos identificar tu cuenta.');
              const dto: OportunidadLaboralDTO = {
                ...initial,
                titulo: form.titulo.trim(),
                contenido: form.contenido.trim(),
                ubicacion: form.ubicacion.trim(),
                modalidad: form.modalidad,
                rangoSalarial: form.rangoSalarial.trim() || 'A convenir',
                activa: initial?.activa ?? true,
                autor: {
                  id: userId,
                  nombre: perfil.data.nombre,
                  email: perfil.data.email,
                },
                etiquetas: form.especialidad
                  ? [
                      tags.data?.find((t) => t.nombre === form.especialidad) ?? {
                        nombre: form.especialidad,
                      },
                      ...(initial?.etiquetas?.slice(1) ?? []),
                    ]
                  : [],
              };
              const result = initial?.id
                ? await oportunidadesService.actualizar(initial.id, dto)
                : await oportunidadesService.crear(dto);
              await refresh();
              router.push(`/empleador/solicitudes/${result.id}`);
            } catch (err) {
              setError(err);
            } finally {
              setBusy(false);
            }
          }}
        >
          <Feedback error={error} />
          <Field label="Título del trabajo">
            <input
              className={control}
              required
              maxLength={150}
              value={form.titulo}
              onChange={(e) => update('titulo', e.target.value)}
              placeholder="Ej.: Reparación de panel LED"
            />
          </Field>
          <Field label="Descripción">
            <textarea
              className={control}
              required
              rows={6}
              maxLength={5000}
              value={form.contenido}
              onChange={(e) => update('contenido', e.target.value)}
              placeholder="¿Qué necesitás resolver? Agregá los detalles del trabajo."
            />
          </Field>
          <Field label="Especialidad">
            <select
              className={control}
              required
              value={form.especialidad}
              onChange={(e) => update('especialidad', e.target.value)}
            >
              <option value="">Elegí una especialidad</option>
              {tags.data?.map((t) => (
                <option key={t.id ?? t.nombre} value={t.nombre}>
                  {t.nombre}
                </option>
              ))}
            </select>
          </Field>
          <QueryStatus pending={tags.isPending} error={tags.error} />
          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Ubicación">
              <input
                className={control}
                required
                maxLength={100}
                value={form.ubicacion}
                onChange={(e) => update('ubicacion', e.target.value)}
                placeholder="Barrio y ciudad"
              />
            </Field>
            <Field label="Presupuesto (opcional)">
              <input
                className={control}
                maxLength={100}
                value={form.rangoSalarial}
                onChange={(e) => update('rangoSalarial', e.target.value)}
                placeholder="A convenir"
              />
            </Field>
          </div>
          <Field label="Modalidad">
            <select
              className={control}
              value={form.modalidad}
              onChange={(e) => update('modalidad', e.target.value)}
            >
              <option value="PRESENCIAL">Presencial</option>
              <option value="REMOTO">Remoto</option>
              <option value="HIBRIDO">Híbrido</option>
            </select>
          </Field>
          <div className="flex justify-end gap-3">
            <Link href="/empleador/solicitudes">
              <Button variant="outline" type="button">
                Cancelar
              </Button>
            </Link>
            <Button disabled={busy || !tags.data || !perfil.data} type="submit">
              {busy ? 'Guardando…' : initial ? 'Guardar cambios' : 'Publicar solicitud'}
            </Button>
          </div>
        </form>
        <aside className={`${panel} h-fit space-y-4`}>
          <h2 className="font-bold">Antes de publicar</h2>
          <p className="text-sm text-zinc-400">
            Un título claro y una descripción detallada ayudan a recibir mejores
            propuestas.
          </p>
          <hr className="border-[#30302f]" />
          <h3 className="font-bold">Resumen</h3>
          <p className="text-sm text-zinc-400">
            {form.especialidad || 'Sin especialidad'} · {form.modalidad.toLowerCase()}
          </p>
          <p className="text-sm text-zinc-400">
            {form.ubicacion || 'Completá la ubicación'}
          </p>
          <p className="text-sm text-[#ffc107]">
            {form.rangoSalarial || 'Presupuesto a convenir'}
          </p>
        </aside>
      </div>
    </div>
  );
}
